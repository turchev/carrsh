package com.github.turchev.views.client;

import com.github.turchev.data.entity.Client;
import com.github.turchev.data.service.ClientService;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.binder.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

class ClientDialogAdd extends ClientDialogAbstract {
	private static final Logger LOG = LogManager.getLogger();
	private static final String LABEL = "Создать запись о клиенте";
	private final ClientService clientService;

	ClientDialogAdd(ClientService clientService) {
		super.label.setText(LABEL);
		this.clientService = clientService;
		LOG.debug("Created ClientWindowAdd");
	}

	@Override
	protected void btnCancelClick() {
		close();
	}

	@Override
	protected void btnAppleClick() {
		try {
			Client client = new Client();
			binder.writeBean(client);
			clientService.update(client);
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
