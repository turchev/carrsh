package com.github.turchev.views.client;

import com.github.turchev.data.entity.Client;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;

abstract class ClientDialogAbstract extends Dialog {

    TextField txtLastName, txtFirstName, txtPatronymic, txtPhone;
    Binder<Client> binder;
    Label label = new Label("");

    ClientDialogAbstract() {

        binder = new Binder<>(Client.class);
        binder.setBean(new Client());

        txtFirstName = new TextField("Имя");
        binder.forField(txtFirstName)
                .withValidator(new RegexpValidator("Допустимы только символы русского алфавита и дефис ",
                        "[а-яА-Я]+-?[а-яА-Я]+"))
                .withValidator(new StringLengthValidator("Максимум 40 символов", 0, 40))
                .bind(Client::getFirstName, Client::setFirstName);
        txtFirstName.setSizeFull();

        txtLastName = new TextField("Фамилия");
        binder.forField(txtLastName)
                .withValidator(new RegexpValidator("Допустимы только символы русского алфавита и дефис ",
                        "[а-яА-Я]+-?[а-яА-Я]+"))
                .withValidator(new StringLengthValidator("Максимум 40 символов", 0, 40))
                .bind(Client::getLastName, Client::setLastName);
        txtLastName.setSizeFull();

        txtPatronymic = new TextField("Отчество");
        binder.forField(txtPatronymic)
                .withValidator(new RegexpValidator("Допустимы только символы русского алфавита и дефис ",
                        "[а-яА-Я]+-?[а-яА-Я]+"))
                .withValidator(new StringLengthValidator("Максимум 40 символов", 0, 40))
                .bind(Client::getPatronymic, Client::setPatronymic);
        txtPatronymic.setSizeFull();

        txtPhone = new TextField("Телефон");
        txtPhone.setValueChangeMode(ValueChangeMode.EAGER);
        binder.forField(txtPhone)
                .withValidator(new RegexpValidator("Введите номер в формате +7(XXX)XXX-XX-XX",
                        "^\\+7\\([0-9]{3}\\)[0-9]{3}\\-[0-9]{2}\\-[0-9]{2}$"))
                .bind(Client::getPhone, Client::setPhone);
        txtPhone.setSizeFull();

        VerticalLayout vlLayout = new VerticalLayout(label, txtLastName, txtFirstName, txtPatronymic, txtPhone);
        Button btnApple = new Button("Ok");
        btnApple.addClickListener(event -> btnAppleClick());
        Button btnCancel = new Button("Отменить");
        btnCancel.addClickListener(event -> btnCancelClick());
        HorizontalLayout hLayout = new HorizontalLayout(btnApple, btnCancel);
        vlLayout.add(hLayout);
        this.add(vlLayout);

    }

    abstract void btnCancelClick();

    abstract void btnAppleClick();

}
