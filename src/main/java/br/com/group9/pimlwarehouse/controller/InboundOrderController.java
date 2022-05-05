package br.com.group9.pimlwarehouse.controller;

import br.com.group9.pimlwarehouse.dto.*;
import br.com.group9.pimlwarehouse.entity.BatchStock;
import br.com.group9.pimlwarehouse.entity.InboundOrder;
import br.com.group9.pimlwarehouse.service.BatchStockService;
import br.com.group9.pimlwarehouse.service.InboundOrderService;
import br.com.group9.pimlwarehouse.service.ProductAPIService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.util.List;
import java.util.Map;

@Api(value = "Inbound Order")
@RestController
public class InboundOrderController extends APIController{
    private InboundOrderService inboundOrderService;
    private BatchStockService batchStockService;
    private ProductAPIService productAPIService;

    public InboundOrderController(
            InboundOrderService inboundOrderService,
            BatchStockService batchStockService,
            ProductAPIService productAPIService
    ) {
        this.inboundOrderService = inboundOrderService;
        this.batchStockService = batchStockService;
        this.productAPIService = productAPIService;
    }

    /**
     * POST method to create an InboundOrder.
     * @param order generate order payload.
     * @param uriBuilder Injection used by Spring to send the location.
     * @return URI of InboundOrder on header location, the entity response with status code "201-Created".
     * Register a batch with product stock.
     */
    @ApiOperation(value = "Register a new Inbound Order")
    @PostMapping("/fresh-products/inboundorder")
    public ResponseEntity<List<BatchStockDTO>> createInboundOrder(
            @RequestBody InboundOrderDTO order, UriComponentsBuilder uriBuilder
    ){

        List<Map<ProductDTO, BatchStockDTO>> batchStocks = productAPIService.getProductInfo(order.getBatchStockList());
        // Salvando a ordem
        InboundOrder orderSaved = inboundOrderService.save(
            order.convert(),
            BatchStockDTO.convert(batchStocks, order.convert())
        );
        List<BatchStockDTO> batchStockDTOS = BatchStockDTO.convert(orderSaved.getBatchStocks());
        URI uri = uriBuilder
                .path("/fresh-products/inboundorder")
                .buildAndExpand(orderSaved.getId())
                .toUri();

        return ResponseEntity.created(uri).body(batchStockDTOS);
    }

    /**
     * PUT method to update List BatchStock
     * @param order generate order payload
     * @param uriBuilder Injection used by Spring to send the location
     * @return URI of InboundOrder on header location, the entity response with status code "201-Created" and update in BatchStock
     */
    @ApiOperation(value = "Updates Batch Stock list of a Inbound Order")
    @PutMapping("/fresh-products/inboundorder")
    public ResponseEntity<List<UpdateBatchStockDTO>> update(
            @RequestBody UpdateInboundOrderDTO order , UriComponentsBuilder uriBuilder
    ){
        InboundOrder orderToUpdate = inboundOrderService.get(order.getOrderNumber());
        List<BatchStock> batchStocks = UpdateBatchStockDTO.convert(order.getBatchStockList() ,orderToUpdate);
        // Salvando os lotes
        List<BatchStock> inboundOrderUpdated = batchStockService.update(
                batchStocks, orderToUpdate
        );
        List<UpdateBatchStockDTO> batchStockDTOS = UpdateBatchStockDTO.convert(inboundOrderUpdated);
        URI uri = uriBuilder
                .path("/fresh-products/inboundorder")
                .buildAndExpand(orderToUpdate.getId())
                .toUri();

        return ResponseEntity.created(uri).body(batchStockDTOS);
    }
}
