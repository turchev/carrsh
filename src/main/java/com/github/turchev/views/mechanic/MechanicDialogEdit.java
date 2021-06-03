package com.github.turchev.views.mechanic;

import com.github.turchev.data.entity.Mechanic;
import com.github.turchev.data.service.MechanicService;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.binder.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Optional;

class MechanicDialogEdit extends MechanicDialogAbstract {
	private static final Logger LOG = LogManager.getLogger();
	private static final String LABEL = "Редактировать данные механика";
	private final Long id;
	private Mechanic mechanic;
	private final MechanicService mechanicService;

	protected MechanicDialogEdit(Long id, MechanicService mechanicService){
		super.label.setText(LABEL);
		this.mechanicService = mechanicService;
		this.id = id;
		Optional<Mechanic> mechanicFromBackend = mechanicService.get(this.id);
		if (mechanicFromBackend.isPresent()) {
			this.mechanic = mechanicFromBackend.get();
			super.txtFirstName.setValue(mechanic.getFirstName());
			super.txtLastName.setValue(mechanic.getLastName());
			super.txtPatronymic.setValue(mechanic.getPatronymic());
			super.dcfWages.setValue(mechanic.getWages());
		} else {
			Notification.show("No data on backend ");
		}
		LOG.debug("Created MechanicWindowEdit");
	}

	@Override
	protected void btnCancelClick() {
		close();
	}

	@Override
	protected synchronized void btnAppleClick() {
		try {
			super.binder.writeBean(this.mechanic);
			mechanic.setId(id);
			mechanicService.update(this.mechanic);
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
