package com.github.turchev.views.order;

import com.github.turchev.data.entity.Order;
import com.github.turchev.data.entity.OrderStatusType;
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

class OrdersDialogAdd extends OrdersDialogAbstract {
    private static final Logger LOG = LogManager.getLogger();
    private static final String LABEL = "Создание заявки";
    private final OrderService orderService;

    protected OrdersDialogAdd(OrderService orderService, ClientService clientService, MechanicService mechanicService) {
        super.label.setText(LABEL);
        this.orderService = orderService;
        mechanicList = mechanicService.findAll();
        clientList = clientService.findAll();
        cmbClient.setItems(clientList);
        cmbMechanic.setItems(mechanicList);
        super.cmbOrderStatusType.setValue(OrderStatusType.Принят);

        /*
          При оформлении новой записи ограничения создания с текущей даты -1час до
          +10дней
         */
        binder.forField(super.dtfDateCreate)
                .withValidator(new DateTimeRangeValidator("Введите корректную дату создания заявки",
                        LocalDateTime.now().minusHours(1), LocalDateTime.now().plusDays(10)))
                .bind(Order::getDateCreate, Order::setDateCreate);
        super.dtfDateCreate.setValue(LocalDateTime.now());

        /*
          При оформлении новой записи ограничения на завершения работ с текущей даты
          -1час до +5лет
         */
        binder.forField(dtfCompletionDate)
                .withValidator(new DateTimeRangeValidator("Введите корректную дату завершения работ",
                        LocalDateTime.now().minusHours(1), LocalDateTime.now().plusYears(5)))
                .bind(Order::getCompletionDate, Order::setCompletionDate);


        LOG.debug("Created OrdersWindowAdd");
    }

    @Override
    protected void btnCancelClick() {
        close();
    }

    @Override
    protected synchronized void btnAppleClick() {
        if (txrDescription.isEmpty()) {
            Notification.show("Описание заявки не может быть пустым", 4000, Position.MIDDLE);
            return;
        }
        if (dtfDateCreate.isEmpty()) {
            Notification.show("Укажите дату и время создания заявки", 4000, Position.MIDDLE);
            return;
        }
        if (dtfCompletionDate.isEmpty()) {
            Notification.show("Укажите дату и время завершения работ", 4000, Position.MIDDLE);
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

        try {
			Order order = new Order();
            super.binder.writeBean(order);
            order.setClient(cmbClient.getValue());
            order.setMechanic(cmbMechanic.getValue());
            order.setStatus(cmbOrderStatusType.getValue());
            orderService.update(order);
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
