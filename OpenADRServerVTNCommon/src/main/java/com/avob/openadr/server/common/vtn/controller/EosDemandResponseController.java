package com.avob.openadr.server.common.vtn.controller;

import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.datatype.DatatypeConfigurationException;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.avob.openadr.server.common.vtn.models.demandresponseevent.DemandResponseEvent;
import com.avob.openadr.server.common.vtn.models.demandresponseevent.DemandResponseEventDto;
import com.avob.openadr.server.common.vtn.models.demandresponseevent.DemandResponseEventDtoValidator;
import com.avob.openadr.server.common.vtn.models.demandresponseevent.DemandResponseEventSimpleValueEnum;
import com.avob.openadr.server.common.vtn.models.demandresponseevent.DemandResponseEventStateEnum;
import com.avob.openadr.server.common.vtn.service.DemandResponseEventService;
import com.avob.openadr.server.common.vtn.service.dtomapper.DtoMapper;

@RestController
@RequestMapping("/DemandResponseEvent")
public class EosDemandResponseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(EosDemandResponseController.class);

	@Resource
	private DtoMapper dtoMapper;

	@Autowired
	private DemandResponseEventService demandResponseEventService;

	@InitBinder
	protected void initBinder(WebDataBinder binder) throws DatatypeConfigurationException {
		binder.setValidator(new DemandResponseEventDtoValidator());
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	public List<DemandResponseEventDto> list(@RequestParam(value = "ven", required = false) String ven,
			@RequestParam(value = "state", required = false) DemandResponseEventStateEnum state) {

		List<DemandResponseEvent> find = demandResponseEventService.find(ven, state);
		return dtoMapper.mapList(find, DemandResponseEventDto.class);
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ResponseBody
	public DemandResponseEventDto create(@Valid @RequestBody DemandResponseEventDto event,
			HttpServletResponse response) {
		DemandResponseEvent map = dtoMapper.map(event, DemandResponseEvent.class);

		DemandResponseEvent save = demandResponseEventService.create(map);

		response.setStatus(HttpStatus.CREATED_201);

		LOGGER.info("create DR event: " + save.toString());

		return dtoMapper.map(save, DemandResponseEventDto.class);

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public DemandResponseEventDto read(@PathVariable(value = "id") Long id, HttpServletResponse response) {
		Optional<DemandResponseEvent> op = demandResponseEventService.findById(id);
		if (!op.isPresent()) {
			response.setStatus(HttpStatus.NOT_FOUND_404);
			return null;
		}
		DemandResponseEvent event = op.get();
		LOGGER.info("read DR event: " + event.toString());
		return dtoMapper.map(event, DemandResponseEventDto.class);

	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public void delete(@PathVariable(value = "id") Long id, HttpServletResponse response) {
		boolean delete = demandResponseEventService.delete(id);
		if (!delete) {
			response.setStatus(HttpStatus.NOT_FOUND_404);
		}
	}

	@RequestMapping(value = "/{id}/cancel", method = RequestMethod.POST)
	@ResponseBody
	public DemandResponseEventDto cancel(@PathVariable(value = "id") Long id, HttpServletResponse response) {
		DemandResponseEvent event = demandResponseEventService.cancel(id);
		if (event == null) {
			response.setStatus(HttpStatus.NOT_FOUND_404);
			return null;
		}
		return dtoMapper.map(event, DemandResponseEventDto.class);
	}

	@RequestMapping(value = "/{id}/active", method = RequestMethod.POST)
	@ResponseBody
	public DemandResponseEventDto active(@PathVariable(value = "id") Long id, HttpServletResponse response) {
		DemandResponseEvent event = demandResponseEventService.active(id);
		if (event == null) {
			response.setStatus(HttpStatus.NOT_FOUND_404);
			return null;
		}
		return dtoMapper.map(event, DemandResponseEventDto.class);
	}

	@RequestMapping(value = "/{id}/normal", method = RequestMethod.POST)
	@ResponseBody
	public DemandResponseEventDto updateValueNormal(@PathVariable(value = "id") Long id, HttpServletResponse response) {
		DemandResponseEvent event = demandResponseEventService.updateValue(id,
				DemandResponseEventSimpleValueEnum.SIMPLE_SIGNAL_PAYLOAD_NORMAL);
		if (event == null) {
			response.setStatus(HttpStatus.NOT_FOUND_404);
			return null;
		}
		return dtoMapper.map(event, DemandResponseEventDto.class);
	}

	@RequestMapping(value = "/{id}/moderate", method = RequestMethod.POST)
	@ResponseBody
	public DemandResponseEventDto updateModerate(@PathVariable(value = "id") Long id, HttpServletResponse response) {
		DemandResponseEvent event = demandResponseEventService.updateValue(id,
				DemandResponseEventSimpleValueEnum.SIMPLE_SIGNAL_PAYLOAD_MODERATE);
		if (event == null) {
			response.setStatus(HttpStatus.NOT_FOUND_404);
			return null;
		}
		return dtoMapper.map(event, DemandResponseEventDto.class);
	}

	@RequestMapping(value = "/{id}/high", method = RequestMethod.POST)
	@ResponseBody
	public DemandResponseEventDto updateValueHigh(@PathVariable(value = "id") Long id, HttpServletResponse response) {
		DemandResponseEvent event = demandResponseEventService.updateValue(id,
				DemandResponseEventSimpleValueEnum.SIMPLE_SIGNAL_PAYLOAD_HIGH);
		if (event == null) {
			response.setStatus(HttpStatus.NOT_FOUND_404);
			return null;
		}
		return dtoMapper.map(event, DemandResponseEventDto.class);
	}

	@RequestMapping(value = "/{id}/special", method = RequestMethod.POST)
	@ResponseBody
	public DemandResponseEventDto updateValueSpecial(@PathVariable(value = "id") Long id,
			HttpServletResponse response) {
		DemandResponseEvent event = demandResponseEventService.updateValue(id,
				DemandResponseEventSimpleValueEnum.SIMPLE_SIGNAL_PAYLOAD_SPECIAL);
		if (event == null) {
			response.setStatus(HttpStatus.NOT_FOUND_404);
			return null;
		}
		return dtoMapper.map(event, DemandResponseEventDto.class);
	}

}
