package com.github.turchev.views.client;

import com.github.turchev.data.entity.Client;
import com.github.turchev.data.service.ClientService;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.binder.ValidationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

class ClientDialogEdit extends ClientDialogAbstract {
	private static final Logger LOG = LogManager.getLogger();
	private static final String LABEL = "Редактироваие данных клиента";
	private Client client;
	private final Long id;
	private final ClientService clientService;

	ClientDialogEdit(Long id, ClientService clientService)  {
		super.label.setText(LABEL);
		this.clientService = clientService;
		this.id = id;
		Optional<Client> clientFromBackend = clientService.get(this.id);
		if (clientFromBackend.isPresent()) {
			this.client = clientFromBackend.get();
			super.txtFirstName.setValue(client.getFirstName());
			super.txtLastName.setValue(client.getLastName());
			super.txtPatronymic.setValue(client.getPatronymic());
			super.txtPhone.setValue(client.getPhone());
		} else {
			Notification.show("No data on backend ");
		}
		LOG.debug("Created ClientWindowEdit");
	}

	@Override
	void btnCancelClick() {
		close();
	}

	@Override
	synchronized void btnAppleClick() {
		try {
			super.binder.writeBean(this.client);
			client.setId(id);
			clientService.update(this.client);
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
