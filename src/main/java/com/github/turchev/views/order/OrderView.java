package com.github.turchev.views.order;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Optional;

import com.github.turchev.data.entity.Client;
import com.github.turchev.data.entity.Order;
import com.github.turchev.data.entity.OrderStatusType;
import com.github.turchev.data.service.ClientService;
import com.github.turchev.data.service.MechanicService;
import com.github.turchev.data.service.OrderService;
import com.github.turchev.views.AbstractView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.github.turchev.views.main.MainView;
import com.vaadin.flow.router.RouteAlias;

@Route(value = "order/:orderID?/:action?(edit)", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Список заказов")
public class OrderView extends AbstractView implements BeforeEnterObserver {

    private final String ORDER_ID = "orderID";
    private final String ORDER_EDIT_ROUTE_TEMPLATE = "order/%d/edit";
    private final Grid<Order> grid = new Grid<>(Order.class, false);

    // Filtering Form
    private TextArea txtDescription;
    private ComboBox<OrderStatusType> cmbStatus;
    private ComboBox<Client> cmbClientLastName;
    private final Button btnAppleFilter = new Button("Применить");
    private final Button btnClearFilter = new Button("Очистить");

    private Order order;
    private final MechanicService mechanicService;
    private final ClientService clientService;
    private final OrderService orderService;
    protected List<Client> clientList;

    public OrderView(@Autowired OrderService orderService,
                     @Autowired ClientService clientService,
                     @Autowired MechanicService mechanicService) {

        this.orderService = orderService;
        this.clientService = clientService;
        this.mechanicService = mechanicService;

        addClassName("order-view");

        SplitLayout splitLayout = new SplitLayout();
        splitLayout.setSizeFull();
        createGridLayout(splitLayout);
        createFilerLayout(splitLayout);
        add(splitLayout);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addColumn("id").setHeader("Id").setAutoWidth(true);
        grid.addColumn("description").setHeader("Описание").setWidth("20%");
        grid.addColumn("client").setHeader("Клиент ФИО").setAutoWidth(true);
        grid.addColumn("mechanic").setHeader("Механик ФИО").setAutoWidth(true);
        grid.addColumn("status").setHeader("Статус").setAutoWidth(true);
        grid.addColumn(new LocalDateTimeRenderer<>(Order::getDateCreate, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
                .setHeader("Дата создания заявки").setSortable(true).setAutoWidth(true);
        grid.addColumn(new LocalDateTimeRenderer<>(Order::getCompletionDate, DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)))
                .setHeader("Дата окончания работ").setSortable(true).setAutoWidth(true);
        grid.addColumn(new NumberRenderer<>(Order::getPrice, NumberFormat.getCurrencyInstance())).setHeader("Стоимость").setId("price");
        grid.setItems(orderService.findAll());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(ORDER_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(OrderView.class);
            }
        });

        btnClearFilter.addClickListener(e -> {
            grid.setItems(orderService.findAll());
            clearForm();
            refreshGrid();
        });

        btnAppleFilter.addClickListener(e -> {
            String sqlLikeDescription, sqlLikeStatus, sqlLikeClientLastName;

            if (txtDescription.getValue() == null || txtDescription.getValue().isEmpty()) {
                sqlLikeDescription = "%";
            } else {
                sqlLikeDescription = "%" + txtDescription.getValue() + "%";
            }
            if (cmbStatus.getValue() == null || cmbStatus.isEmpty()) {
                sqlLikeStatus = "%";
            } else {
                sqlLikeStatus = "%" + cmbStatus.getValue().toString() + "%";
            }
            if (cmbClientLastName.getValue() == null || cmbClientLastName.isEmpty()) {
                sqlLikeClientLastName = "%";
            } else {
                sqlLikeClientLastName = "%" + cmbClientLastName.getValue().getLastName() + "%";
            }

            List<Order> orderList = orderService.findUsingFilter(sqlLikeDescription, sqlLikeStatus, sqlLikeClientLastName);
            grid.setItems(orderList);
        });
    }

    @Override
//    executed before navigation
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> orderId = event.getRouteParameters().getLong(ORDER_ID);
        if (orderId.isPresent()) {
            Optional<Order> orderFromBackend = orderService.get(orderId.get());
            if (orderFromBackend.isPresent()) {
                populateForm(orderFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested order was not found, ID = %d", orderId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(OrderView.class);
            }
        }
    }

    @Override
    protected void btnAddClick() {
        try {
            OrdersDialogAdd subWindowAdd = new OrdersDialogAdd(
                    orderService, clientService, mechanicService);
            subWindowAdd.open();
            refreshGridAfterClosingDialog(subWindowAdd);
        } catch (Exception e) {
            LOG.error(e);
            Notification.show("Ошибка диалогового окна создания записи");
        }
    }

    @Override
    protected void btnChangeClick() {
        try {
            if (grid.asSingleSelect().isEmpty()) {
                Notification.show("Выберите заказ из списка", 4000, Notification.Position.MIDDLE);
                return;
            }
            OrdersDialogEdit subWindowEdit = new OrdersDialogEdit(
                    orderService, clientService, mechanicService, this.order);
            subWindowEdit.open();
            refreshGridAfterClosingDialog(subWindowEdit);
        } catch (Exception e) {
            LOG.error(e);
            Notification.show("Ошибка диалогового окна редактирования", 10000, Notification.Position.MIDDLE);
        }
    }

    @Override
    protected void btnDeleteClick() {
        try {
            if (grid.asSingleSelect().isEmpty()) {
                Notification.show("Выберите заказ из списка");
                return;
            }
            Order selectedOrders = grid.asSingleSelect().getValue();
            final String MESSAGE_1 = "Удалить запись №" + selectedOrders.getId() +
                    " " + selectedOrders.getDescription() + "?";

            ConfirmDialog
                    .createQuestion()
                    .withCaption("Внимание")
                    .withMessage(MESSAGE_1)
                    .withOkButton(() -> {
                        try {
                            orderService.delete(selectedOrders.getId());
                            clearForm();
                            refreshGrid();
                        } catch (Exception ex) {
                            LOG.error(ex);
                        }
                    }, ButtonOption.focus(), ButtonOption.caption("Подтвердить"))
                    .withCancelButton(ButtonOption.caption("Отменить"))
                    .open();
        } catch (Exception e) {
            Notification.show("Не удалось выполнить удаление", 10000, Notification.Position.MIDDLE);
        }
    }

    //    creating a filter / preview panel
    private void createFilerLayout(SplitLayout splitLayout) {
        Div filterLayoutDiv = new Div();
        filterLayoutDiv.setId("filter-layout");

        Div filterDiv = new Div();
        filterDiv.setId("filter");
        filterLayoutDiv.add(filterDiv);

        FormLayout formLayout = new FormLayout();
        HorizontalLayout label = new HorizontalLayout();
        label.add(new H4("Предпросмотр / Фильтр"));
        txtDescription = new TextArea("Описание");
        txtDescription.setMinHeight("200px");
        cmbStatus = new ComboBox<>("Статус", OrderStatusType.values());
        cmbClientLastName = new ComboBox<>("Фамилия клиента", clientService.findAll());

        Component[] fields = new Component[]{label, txtDescription, btnAppleFilter, cmbStatus, cmbClientLastName, btnClearFilter};
        for (Component field : fields) {
            ((HasStyle) field).addClassName("full-width");
        }
        formLayout.add(fields);
        filterDiv.add(formLayout);
        createButtonLayout(filterLayoutDiv);

        splitLayout.addToSecondary(filterLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setId("button-layout");
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        btnAppleFilter.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        btnClearFilter.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(btnAppleFilter, btnClearFilter);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
//        grid.getDataProvider().refreshAll();
        grid.getLazyDataView().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Order value) {
        this.order = value;
        if (value == null) {
            txtDescription.clear();
            cmbClientLastName.clear();
            cmbStatus.clear();
        } else {
            if (order.getDescription() != null){
                txtDescription.setValue(order.getDescription());
            }
        }
    }

    private <T extends OrdersDialogAbstract> void refreshGridAfterClosingDialog(T dialog) {
        dialog.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                grid.setItems(orderService.findAll());
            }
        });
    }
}
