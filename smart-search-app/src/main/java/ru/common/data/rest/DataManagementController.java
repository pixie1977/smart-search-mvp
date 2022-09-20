package ru.common.data.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.common.data.dto.User;
import ru.common.data.dto.UserOperationCode;
import ru.common.data.dto.UserOperationMessage;
import ru.common.data.services.UserManagementService;
import ru.common.search.dto.AddResponse;
import ru.common.data.services.ProducerService;


@RestController
@RequestMapping("/data")
public class DataManagementController {

	private static final Logger LOG = LoggerFactory.getLogger(DataManagementController.class);

	private final ProducerService producerService;
	private final UserManagementService userManagementService;

	@Autowired
	public DataManagementController(
			ProducerService producerService,
			UserManagementService userManagementService
	){
		this.producerService = producerService;
		this.userManagementService = userManagementService;
	}

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<AddResponse> add(@RequestBody User user) {
		AddResponse response = new AddResponse();

		if(user == null){
			response.setError("User cannot be null");
			return ResponseEntity.ok(response);
		}

		boolean res = userManagementService.saveUser(user);
		if(res){
			UserOperationMessage message = new UserOperationMessage();
			message.setCode(UserOperationCode.ADD);
			user.setPassword("");
			message.setUser(user);
			producerService.broadcastMessage(message);
		}

		return ResponseEntity.ok(response);
	}
}