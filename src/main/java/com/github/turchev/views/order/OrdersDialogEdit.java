package com.github.turchev.views.order;

import com.github.turchev.data.entity.Order;
import com.github.turchev.data.service.ClientService;
import com.github.turchev.data.service.MechanicService;
import com.github.turchev.data.service.OrderService;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.DateTimeRangeValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDateTime;

class OrdersDialogEdit extends OrdersDialogAbstract {
    private static final Logger LOG = LogManager.getLogger();
    private static final String LABEL = "Редактирование заявки";
    private final OrderService orderService;
    private final Order order;

    OrdersDialogEdit(OrderService orderService, ClientService clientService, MechanicService mechanicService, Order order) {
        super.label.setText(LABEL);
        this.order = order;
        this.orderService = orderService;
        mechanicList = mechanicService.findAll();
        clientList = clientService.findAll();
        cmbClient.setItems(clientList);
        cmbClient.setValue(this.order.getClient());
        cmbMechanic.setItems(mechanicList);
        cmbMechanic.setValue(this.order.getMechanic());


        /*
          При редактировании записи ограничения создания заявки -5 лет от текущей даты
          до +5 лет
         */
        binder.forField(dtfDateCreate)
                .withValidator(new DateTimeRangeValidator("Дата создания вне диапазона",
                        LocalDateTime.now().minusYears(5), LocalDateTime.now().plusYears(5)))
                .bind(Order::getDateCreate, Order::setDateCreate);

        /*
          При редактировании записи ограничения даты завершения работ -5 лет от текущей
          даты до +5 лет
         */
        binder.forField(dtfCompletionDate)
                .withValidator(new DateTimeRangeValidator("Дата завершения работ вне диапазона",
                        LocalDateTime.now().minusYears(5), LocalDateTime.now().plusYears(5)))
                .bind(Order::getCompletionDate, Order::setCompletionDate);

        txrDescription.setValue(order.getDescription());
        dtfDateCreate.setValue(order.getDateCreate());
        dtfCompletionDate.setValue(order.getCompletionDate());
        cmbOrderStatusType.setValue(order.getStatus());
        dcfPrice.setValue(order.getPrice());

        LOG.debug("Created OrdersWindowEdit");
    }

    @Override
    protected void btnCancelClick() {
        close();
    }

    @Override
    protected void btnAppleClick() {
        if (txrDescription.isEmpty()) {
            Notification.show("Описание заявки не может быть пустым", 4000, Position.MIDDLE);
            return;
        }
        if (cmbClient.isEmpty()) {
            Notification.show("Выберите клиента из списка или создайте новую запись", 4000, Position.MIDDLE);
            return;
        }
        if (cmbMechanic.isEmpty()) {
            Notification.show("Выберите механика из списка или создайте новую запись", 4000, Position.MIDDLE);
            return;
        }
        if (cmbOrderStatusType.isEmpty()) {
            Notification.show("Задайте статус заявки", 4000, Position.MIDDLE);
            return;
        }
        if (dtfDateCreate.isEmpty()) {
            Notification.show("Укажите дату заявки", 4000, Position.MIDDLE);
            return;
        }

        try {
            super.binder.writeBean(this.order);
            order.setClient(cmbClient.getValue());
            order.setMechanic(cmbMechanic.getValue());
            order.setStatus(cmbOrderStatusType.getValue());
            orderService.update(this.order);
            close();
        } catch (ValidationException ev) {
            LOG.debug(ev);
            Notification.show("Проверьте корректность заполнения полей данных");
        } catch (Exception e) {
            LOG.error(e);
            Notification.show("Не удалось сохранить запись");
        }
    }
}
