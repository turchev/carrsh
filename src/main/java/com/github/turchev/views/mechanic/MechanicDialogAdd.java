package com.github.turchev.views.mechanic;

import com.github.turchev.data.entity.Mechanic;
import com.github.turchev.data.service.MechanicService;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.binder.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class MechanicDialogAdd extends MechanicDialogAbstract {
	private static final Logger LOG = LogManager.getLogger();
	private static final String LABEL = "Создать запись о механике";
	private final MechanicService mechanicService;

	protected MechanicDialogAdd(MechanicService mechanicService){
		super.label.setText(LABEL);
		this.mechanicService = mechanicService;
		LOG.debug("Created MechanicWindowAdd");
	}

	@Override
	protected void btnCancelClick() {
		close();
	}

	@Override
	protected void btnAppleClick() {
		try {
			Mechanic mechanic = new Mechanic();
			binder.writeBean(mechanic);
			mechanicService.update(mechanic);
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
