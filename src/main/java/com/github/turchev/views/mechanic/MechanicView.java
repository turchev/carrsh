package com.github.turchev.views.mechanic;

import com.github.turchev.data.entity.Mechanic;
import com.github.turchev.data.service.MechanicService;
import com.github.turchev.views.AbstractView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import org.claspina.confirmdialog.ButtonOption;
import org.claspina.confirmdialog.ConfirmDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PageTitle;
import com.github.turchev.views.main.MainView;

@Route(value = "mechanic/:mechanicID?/:action?(edit)", layout = MainView.class)
@PageTitle("Список механиков")
public class MechanicView extends AbstractView{

    private final Grid<Mechanic> grid = new Grid<>(Mechanic.class, false);
    private final MechanicService mechanicService;

    public MechanicView(@Autowired MechanicService mechanicService) {
        this.mechanicService = mechanicService;
        addClassName("mechanic-view");

        Button btnShowStat = new Button("Показать статистику");
        super.groupButtons.add(btnShowStat);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        createGridLayout(horizontalLayout);
        add(horizontalLayout);

        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addColumn("lastName").setHeader("Фамилия").setAutoWidth(true);
        grid.addColumn("firstName").setHeader("Имя").setAutoWidth(true);
        grid.addColumn("patronymic").setHeader("Отчество").setAutoWidth(true);
        grid.addColumn("wages").setHeader("Почасовая оплата").setAutoWidth(true);

        grid.setItems(query -> mechanicService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setHeightFull();

        btnShowStat.addClickListener(event -> {
            try {
                MechanicDialogStat subWindowEdit = new MechanicDialogStat(mechanicService);
                subWindowEdit.open();
            } catch (Exception e) {
                LOG.error(e);
                Notification.show("Вывод статистического отчета завершился ошибкой");
            }
        });
    }

    @Override
    protected void btnAddClick() {
        try {
            MechanicDialogAdd subWindowAdd = new MechanicDialogAdd(mechanicService);
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
                Notification.show("Выберите механика из списка");
                return;
            }
            Mechanic selectedMechanic = grid.asSingleSelect().getValue();
            MechanicDialogEdit subWindowEdit = new MechanicDialogEdit(selectedMechanic.getId(), mechanicService);
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
                Notification.show("Выберите механика из списка");
                return;
            }
            Mechanic selectedMechanic = grid.asSingleSelect().getValue();
            final String MESSAGE_1 = "Удаление записи " + selectedMechanic.getLastName() + " "
                    + selectedMechanic.getFirstName() + " " + selectedMechanic.getPatronymic() + "?";
            ConfirmDialog
                    .createWarning()
                    .withCaption("Внимание")
                    .withMessage(MESSAGE_1)
                    .withOkButton(() -> {
                        try {
                            mechanicService.delete(selectedMechanic.getId());
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

    private void createGridLayout(HorizontalLayout layout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        layout.add(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getLazyDataView().refreshAll();
    }

    private <T extends MechanicDialogAbstract> void refreshGridAfterClosingDialog(T dialog) {
        dialog.addOpenedChangeListener(event -> {
            if (!event.isOpened()) {
                refreshGrid();
            }
        });
    }
}
