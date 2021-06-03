package com.github.turchev.views.client;

import com.github.turchev.data.entity.Client;
import com.github.turchev.data.service.ClientService;
import com.github.turchev.views.AbstractView;
import com.github.turchev.views.main.MainView;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.dao.DataIntegrityViolationException;


@Route(value = "client/:clientID?/:action?(edit)", layout = MainView.class)
@PageTitle("Сисок клиентов")
public class ClientView extends AbstractView{

    private final Grid<Client> grid = new Grid<>(Client.class, false);
    private final ClientService clientService;

    public ClientView(@Autowired ClientService clientService) {
        this.clientService = clientService;
        addClassName("client-view");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();

        createGridLayout(horizontalLayout);
        add(horizontalLayout);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addColumn("lastName").setHeader("Фамилия").setAutoWidth(true);
        grid.addColumn("firstName").setHeader("Имя").setAutoWidth(true);
        grid.addColumn("patronymic").setHeader("Отчество").setAutoWidth(true);
        grid.addColumn("phone").setHeader("Телефон").setAutoWidth(true);

        grid.setItems(query -> clientService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();
    }

    @Override
    protected void btnAddClick() {
        try {
            ClientDialogAdd subWindowAdd = new ClientDialogAdd(clientService);
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
                Notification.show("Выберите клиента из списка");
                return;
            }
            Client selectedClient = grid.asSingleSelect().getValue();
            ClientDialogEdit subWindowEdit = new ClientDialogEdit(selectedClient.getId(), clientService);
            subWindowEdit.open();
            refreshGridAfterClosingDialog(subWindowEdit);
        } catch (Exception e) {
            LOG.error(e);
            Notification.show("Ошибка диалогового окна редактирования");
        }
    }

    @Override
    protected void btnDeleteClick() {
        try {
            if (grid.asSingleSelect().isEmpty()) {
                Notification.show("Выберите клиента из списка");
                return;
            }
            Client selectedClient = grid.asSingleSelect().getValue();
            final String MESSAGE_1 = "Удаление записи " + selectedClient.getLastName() + " "
                    + selectedClient.getFirstName() + " " + selectedClient.getPatronymic() + "?";
            ConfirmDialog
                    .createWarning()
                    .withCaption("Внимание")
                    .withMessage(MESSAGE_1)
                    .withOkButton(() -> {
                        try {
                            clientService.delete(selectedClient.getId());
                            refreshGrid();
                        } catch (DataIntegrityViolationException xe) {
                            LOG.error(xe);
                            Notification.show("Предварительно удалите все заказы, связанные с этой записью");
                        }
                    }, ButtonOption.focus(), ButtonOption.caption("Подтвердить"))
                    .withCancelButton(ButtonOption.caption("Отменить"))
                    .open();
        } catch (Exception e) {
            LOG.error(e);
            Notification.show("Не удалось выполнить удаление");
        }
    }

    private void createGridLayout(HorizontalLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.add(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private <T extends ClientDialogAbstract> void refreshGridAfterClosingDialog(T dialog) {
        dialog.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                refreshGrid();
            }
        });
    }
}
