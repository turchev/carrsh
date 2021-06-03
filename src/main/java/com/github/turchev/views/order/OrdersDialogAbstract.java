package com.github.turchev.views.order;

import com.github.turchev.data.entity.Client;
import com.github.turchev.data.entity.Mechanic;
import com.github.turchev.data.entity.Order;
import com.github.turchev.data.entity.OrderStatusType;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.BigDecimalRangeValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.math.BigDecimal;
import java.util.List;

abstract class OrdersDialogAbstract extends Dialog {
    Label label = new Label("");
    ComboBox<Client> cmbClient;
    ComboBox<Mechanic> cmbMechanic;
    ComboBox<OrderStatusType> cmbOrderStatusType;
    DateTimePicker dtfDateCreate, dtfCompletionDate;
    BigDecimalField dcfPrice;
    TextArea txrDescription;
    List<Mechanic> mechanicList;
    List<Client> clientList;
    Binder<Order> binder;

    OrdersDialogAbstract() {
        this.setModal(true);
        binder = new Binder<>(Order.class);
        binder.setBean(new Order());
        cmbOrderStatusType = new ComboBox<>();
        cmbOrderStatusType.setLabel("Статус");
        cmbOrderStatusType.setItems(OrderStatusType.values());
        cmbClient = new ComboBox<>("Клиент ФИО");
        cmbMechanic = new ComboBox<>("Механик ФИО");
        dtfDateCreate = new DateTimePicker("Дата создания заявки");
        dtfCompletionDate = new DateTimePicker("Дата окончания работ");

        txrDescription = new TextArea("Описание заявки");
        binder.forField(txrDescription)
                .withValidator(new StringLengthValidator("Минимум 10, максимум 4500 символов", 10, 4500))
                .bind(Order::getDescription, Order::setDescription);
        txrDescription.setSizeFull();

        dcfPrice = new BigDecimalField("Стоимость");
        dcfPrice.setValueChangeMode(ValueChangeMode.EAGER);
        binder.forField(dcfPrice)
                .withValidator(new BigDecimalRangeValidator("Таких цен не бывает:)", new BigDecimal(0),
                        new BigDecimal(100000000)))
                .bind(Order::getPrice, Order::setPrice);

        HorizontalLayout hltStatusPrice = new HorizontalLayout();
        hltStatusPrice.add(cmbOrderStatusType, dcfPrice);
        HorizontalLayout hltClientMechanic = new HorizontalLayout(cmbClient, cmbMechanic);
        VerticalLayout vlLayout = new VerticalLayout(label, dtfCompletionDate, txrDescription, dtfDateCreate,
                dtfCompletionDate, hltStatusPrice, hltClientMechanic);
        Button btnApple = new Button("Ok");
        btnApple.addClickListener(event -> btnAppleClick());
        Button btnCancel = new Button("Отменить");
        btnCancel.addClickListener(event -> btnCancelClick());
        HorizontalLayout hltButton = new HorizontalLayout(btnApple, btnCancel);
        vlLayout.add(hltButton);
        this.add(vlLayout);
    }

    protected abstract void btnCancelClick();

    protected abstract void btnAppleClick();
}
