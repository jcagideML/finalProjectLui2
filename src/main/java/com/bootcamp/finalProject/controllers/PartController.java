package com.bootcamp.finalProject.controllers;

import com.bootcamp.finalProject.dtos.ErrorDTO;
import com.bootcamp.finalProject.dtos.SubsidiaryResponseDTO;
import com.bootcamp.finalProject.dtos.*;
import com.bootcamp.finalProject.exceptions.InternalExceptionHandler;
import com.bootcamp.finalProject.exceptions.OrderIdNotFoundException;
import com.bootcamp.finalProject.services.IWarehouseService;
import com.bootcamp.finalProject.services.IPartService;
import com.bootcamp.finalProject.utils.ValidationController;
import org.hibernate.QueryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;

import static com.bootcamp.finalProject.utils.ValidationController.isListEndpointMapValid;
import static com.bootcamp.finalProject.utils.ValidationController.validateDateFormat;


@RestController
@RequestMapping("/api/v1/parts")
@Validated
public class PartController {

    @Autowired
    IPartService service;


    @Autowired
    IWarehouseService warehouseService;

    /**
     * GET method to search list of parts, it receives a map with the following data
     * queryType: [“C”,”P”,”V”] -> COMPLETE, PARTIAL, VARIATION
     * date:  date for de query consultation
     * order: [”0”,1”,”2”,”3”] -> orderDate default, orderDate ASC, orderDate DESC, orderDate LastChange
     *
     * @param params map of parameters given by user
     * @return List<PartResponseDTO> that contains the list of parts that have had a change according to the query
     */


    @GetMapping("list")
    public List<PartResponseDTO> findPart(@Nullable @RequestParam Map<String, String> params) throws Exception {
        //Validations
        isListEndpointMapValid(params);
        //Set parameters for PartRequestDto
        PartRequestDTO requestDTO = new PartRequestDTO();
        requestDTO.setQueryType(params.get("queryType"));
        requestDTO.setDate((params.get("date") == null) ? null : validateDateFormat(params.get("date")));
        requestDTO.setOrder((params.get("order") == null | params.get("order").equals("")) ? 0 : Integer.parseInt(params.get("order")));
        //Call to service service
        return service.findPart(requestDTO);
    }

    /**
     * GET method to search orders, it receives a map with the following data
     * dealerNumber:  identification of dealer whose orders are to be looked for
     * deliveryStatus:  ["P","D","F","C"] -> Pending, Delayed, Finished, Cancelled
     * order: ["1","2"] -> orderDate ASC, orderDate DESC
     *
     * @param params map of parameters given by user
     * @return OrderResponseDTO that contains the list of found orders
     */
    @GetMapping("orders")
    public SubsidiaryResponseDTO findSubsidiaryOrders(@RequestParam Map<String, String> params) throws InternalExceptionHandler {
        //Validations
        ValidationController.isOrdersEndpointMapValid(params);
        //Setting values to OrderRequestDTO
        OrderRequestDTO orderRequestDTO = new OrderRequestDTO();
        orderRequestDTO.setDealerNumber(Long.parseLong(params.get("dealerNumber")));
        orderRequestDTO.setDeliveryStatus(params.get("deliveryStatus") == null ? null : params.get("deliveryStatus"));
        orderRequestDTO.setOrder((params.get("order") == null | params.get("order").equals("")) ? 0 : Integer.parseInt(params.get("order")));
        //
        return warehouseService.findSubsidiaryOrders(orderRequestDTO);
    }


    @GetMapping("orders/{orderNumberCM}")
    ///Por algun motivo no se esta haciendo la validacion, esta el tag @validate en el controller tal como la documentacion
    public OrderDTO findByOrderNumberCM(@PathVariable("orderNumberCM") @Pattern(regexp = "^\\d{4}-\\d{3}$") String orderNumberCM) throws InternalExceptionHandler {


        if (!orderNumberCM.matches("^\\d{4}-\\d{3}$")) {
            throw new QueryException("pattern error");
        }

        return warehouseService.findByOrderNumberCM(orderNumberCM);
    }


    @ExceptionHandler(InternalExceptionHandler.class)
    public ResponseEntity<ErrorDTO> handleException(InternalExceptionHandler e) {
        return new ResponseEntity<>(e.getError(), e.getReturnStatus());
    }
}
