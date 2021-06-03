package com.github.turchev.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractView extends Div {

	protected static final Logger LOG = LogManager.getLogger();
	protected HorizontalLayout groupButtons;
	
	protected AbstractView() {
		this.groupButtons = new HorizontalLayout();
		Button btnAdd = new Button("Добавить");
		Button btnChange = new Button("Изменить");
		Button btnDelete = new Button("Удалить");
		btnAdd.addClickListener(event -> btnAddClick());
		btnChange.addClickListener(event -> btnChangeClick());
		btnDelete.addClickListener(event -> btnDeleteClick());
		groupButtons.add(btnAdd);
		groupButtons.add(btnChange);
		groupButtons.add(btnDelete);
		add(this.groupButtons);
	}

	protected abstract void btnAddClick();
	
	protected abstract void btnChangeClick();
	
	protected abstract void btnDeleteClick();
}
