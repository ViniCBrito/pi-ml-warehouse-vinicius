package br.com.group9.pimlwarehouse.service;

import br.com.group9.pimlwarehouse.dto.DistanceResponseElementDTO;
import br.com.group9.pimlwarehouse.entity.*;
import br.com.group9.pimlwarehouse.enums.CategoryENUM;
import br.com.group9.pimlwarehouse.exception.BatchStockWithdrawException;
import br.com.group9.pimlwarehouse.exception.InboundOrderValidationException;
import br.com.group9.pimlwarehouse.repository.BatchStockRepository;
import br.com.group9.pimlwarehouse.service.BatchStockService;
import br.com.group9.pimlwarehouse.service.SectionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class BatchStockServiceTest {
    private BatchStockRepository batchStockRepository;
    private BatchStockService batchStockService;
    private SectionService sectionService;
    private DistanceMatrixAPIService distanceMatrixAPIService;

    @BeforeEach
    public void before() {
        this.batchStockRepository = Mockito.mock(BatchStockRepository.class);
        this.sectionService = Mockito.mock(SectionService.class);
        this.distanceMatrixAPIService = Mockito.mock(DistanceMatrixAPIService.class);
        this.batchStockService = new BatchStockService(batchStockRepository, sectionService, distanceMatrixAPIService);
    }

    @Test
    public void shouldSaveAListOfBatchStocks(){
        List<BatchStock> batchStockList = new ArrayList<>();
        batchStockList.add(new BatchStock());
        batchStockList.add(new BatchStock());
        batchStockList.add(new BatchStock());

        Mockito.when(batchStockRepository.save(any(BatchStock.class))).thenReturn(new BatchStock());

        List<BatchStock> savedBatchStockList = batchStockService.save(batchStockList);
        assertEquals(3, savedBatchStockList.size());
    }
    @Test
    public void shouldReturnBatchStocksByProductId(){
        List<BatchStock> batchStockList = new ArrayList<>();
        batchStockList.add(new BatchStock());
        batchStockList.add(new BatchStock());
        batchStockList.add(new BatchStock());

        Mockito.when(batchStockRepository.findByProductId(any(Long.class))).thenReturn(batchStockList);

        List<BatchStock> savedBatchStockList = batchStockService.findByProductId(1L);
        assertEquals(3, savedBatchStockList.size());
    }

    @Test
    public void shouldUpdateBatchStockInitialQuantity(){

        List<BatchStock> newBatchStocks = new ArrayList<>();
        newBatchStocks.add(BatchStock.builder().batchNumber(1).currentQuantity(121).build());
        newBatchStocks.add(BatchStock.builder().batchNumber(2).currentQuantity(122).build());
        newBatchStocks.add(BatchStock.builder().batchNumber(3).currentQuantity(123).build());
        List<BatchStock> oldBatchStocks = new ArrayList<>();
        oldBatchStocks.add(BatchStock.builder().batchNumber(1).currentQuantity(121).build());
        oldBatchStocks.add(BatchStock.builder().batchNumber(2).currentQuantity(221).build());
        oldBatchStocks.add(BatchStock.builder().batchNumber(3).currentQuantity(321).build());

        InboundOrder order = InboundOrder.builder().batchStocks(oldBatchStocks).build();

        assertThrows(InboundOrderValidationException.class, () -> batchStockService.update(newBatchStocks, null));
        assertDoesNotThrow(() -> batchStockService.update(newBatchStocks, order));

        order.getBatchStocks().remove(1);
        assertThrows(InboundOrderValidationException.class, () -> batchStockService.update(newBatchStocks, order));

    }

    @Test
    public void shouldReturnProductByDueDate() {

        BatchStock batchStock1 = BatchStock.builder().batchNumber(1).dueDate(LocalDate.now().plusMonths(2)).build();
        BatchStock batchStock2 = BatchStock.builder().batchNumber(2).dueDate(LocalDate.now().plusMonths(3)).build();
        BatchStock batchStock3 = BatchStock.builder().batchNumber(3).dueDate(LocalDate.now().plusMonths(4)).build();

        List<BatchStock> orderedBatchStocks = new ArrayList<>();
        orderedBatchStocks.add(batchStock1);
        orderedBatchStocks.add(batchStock2);
        orderedBatchStocks.add(batchStock3);

        List<BatchStock> unorderedBatchStocks = new ArrayList<>();
        unorderedBatchStocks.add(batchStock3);
        unorderedBatchStocks.add(batchStock1);
        unorderedBatchStocks.add(batchStock2);

        Mockito.when(batchStockRepository.findByDueDateBetweenAndCategory(
                any(LocalDate.class), any(LocalDate.class), any(CategoryENUM.class))
        ).thenReturn(unorderedBatchStocks);

        List<BatchStock> batchStockList = batchStockService.getAllBatchesByDueDate(null, 30L, CategoryENUM.FF);
        assertEquals(orderedBatchStocks, batchStockList);


        List<InboundOrder> inboundOrders = Collections.singletonList(
                InboundOrder.builder().batchStocks(unorderedBatchStocks).build()
        );
        Section section = Section.builder().inboundOrders(inboundOrders).build();
        Mockito.when(sectionService.findById(any(Long.class))).thenReturn(section);

        Mockito.when(batchStockRepository.findByDueDateBetweenAndInboundOrder(
                any(LocalDate.class), any(LocalDate.class), any(InboundOrder.class))
        ).thenReturn(unorderedBatchStocks);

        batchStockList = batchStockService.getAllBatchesByDueDate(1L, 30L, null);
        assertEquals(orderedBatchStocks, batchStockList);
    }

    @Test
    public void shouldWithdrawStockByProductId() {

        List<BatchStock> batchStock1 = Collections.singletonList(BatchStock.builder().currentQuantity(9).productId(1L).build());
        List<BatchStock> batchStock2 = Collections.singletonList(BatchStock.builder().currentQuantity(10).productId(1L).build());
        List<BatchStock> batchStock3 = Collections.singletonList(BatchStock.builder().currentQuantity(10).productId(1L).build());

        Map<Long, Integer> quantityByProductMap = new HashMap<>();
        quantityByProductMap.put(1L, 10);
        quantityByProductMap.put(2L, 10);
        quantityByProductMap.put(3L, 10);

        Mockito.when(batchStockService.findByProductIdWithValidShelfLife(1L)).thenReturn(batchStock1);
        Mockito.when(batchStockService.findByProductIdWithValidShelfLife(2L)).thenReturn(batchStock2);
        Mockito.when(batchStockService.findByProductIdWithValidShelfLife(3L)).thenReturn(batchStock3);

        assertThrows(BatchStockWithdrawException.class, () -> batchStockService.withdrawStockByProductId(quantityByProductMap, null));

        quantityByProductMap.remove(1L);
        List<BatchStock> batchStockList = List.of(batchStock2.get(0), batchStock3.get(0));
        Mockito.when(batchStockRepository.saveAll(batchStock2)).thenReturn(batchStock2);
        Mockito.when(batchStockRepository.saveAll(batchStock3)).thenReturn(batchStock3);

        List<BatchStock> batchStocks = batchStockService.withdrawStockByProductId(quantityByProductMap, null);
        assertEquals(batchStocks, batchStockList);
        assertEquals(batchStocks.get(0).getCurrentQuantity(), 0);
        assertEquals(batchStocks.get(1).getCurrentQuantity(), 0);




    }

    @Test
    public void shouldWithdrawProductsByLocation() {
        List<BatchStock> batchStocks1 = createValidBatchStocks();
        List<BatchStock> batchStocks2 = createValidBatchStocks();
        batchStocks2.forEach(b -> b.setProductId(2L));
        Section section1 = createValidSection();
        InboundOrder inboundOrder1 = InboundOrder.builder()
                .batchStocks(Stream.concat(batchStocks1.stream(), batchStocks2.stream()).collect(Collectors.toList()))
                .build();
        inboundOrder1.setSection(section1);
        section1.setInboundOrders(new ArrayList<>(Arrays.asList(inboundOrder1)));
        batchStocks1.forEach(b -> b.setInboundOrder(section1.getInboundOrders().get(0)));
        batchStocks2.forEach(b -> b.setInboundOrder(section1.getInboundOrders().get(0)));

        List<BatchStock> batchStocks3 = createValidBatchStocks();
        List<BatchStock> batchStocks4 = createValidBatchStocks();
        batchStocks4.forEach(b -> b.setProductId(2L));
        Section section2 = createValidSection();
        Warehouse warehouse2 = section2.getWarehouse();
        warehouse2.setId(2L);
        section2.setWarehouse(warehouse2);
        InboundOrder inboundOrder2 = InboundOrder.builder()
                .batchStocks(Stream.concat(batchStocks3.stream(), batchStocks4.stream()).collect(Collectors.toList()))
                .build();
        inboundOrder2.setSection(section2);
        section2.setInboundOrders(new ArrayList<>(Arrays.asList(inboundOrder2)));
        batchStocks3.forEach(b -> b.setInboundOrder(section2.getInboundOrders().get(0)));
        batchStocks4.forEach(b -> b.setInboundOrder(section2.getInboundOrders().get(0)));

        Map<Long, Integer> quantityByProductMap = new HashMap<>();
        quantityByProductMap.put(1L, 2);
        quantityByProductMap.put(2L, 10);

        Map<DistanceResponseElementDTO, Long> distanceResponseMap = new HashMap<>();
        DistanceResponseElementDTO distance1 = DistanceResponseElementDTO.builder()
                .distanceValue(200).build();
        DistanceResponseElementDTO distance2 = DistanceResponseElementDTO.builder()
                .distanceValue(50).build();
        distanceResponseMap.put(distance1, 1L);
        distanceResponseMap.put(distance2, 2L);

        Mockito.when(distanceMatrixAPIService.fetchDistances(Mockito.anyString(), Mockito.any()))
                .thenReturn(distanceResponseMap);
        Mockito.when(batchStockService.findByProductIdWithValidShelfLife(1L))
                .thenReturn(new ArrayList<>(Stream.concat(batchStocks1.stream(), batchStocks3.stream()).collect(Collectors.toList())));
        Mockito.when(batchStockService.findByProductIdWithValidShelfLife(2L))
                .thenReturn(new ArrayList<>(Stream.concat(batchStocks2.stream(), batchStocks4.stream()).collect(Collectors.toList())));

        Assertions.assertDoesNotThrow(() -> {
            batchStockService.withdrawStockByProductId(quantityByProductMap, "abc");
        });

    }

    private Section createValidSection() {
        Address address = Address.builder()
                .address("Test Street")
                .addressNumber(123)
                .addressComplement("")
                .addressDistrict("Test District")
                .postalCode("06020-012")
                .city("São Paulo")
                .state("SP")
                .country("Brasil")
                .placeId("123123")
                .build();
        Warehouse warehouse = Warehouse.builder()
                .id(1L)
                .address(address)
                .build();
        SectionProduct sectionProduct = SectionProduct.builder()
                .id(1L)
                .productId(1L)
                .build();
        InboundOrder inboundOrder = InboundOrder.builder()
                .batchStocks(createValidBatchStocks())
                .build();
        Section section = Section.builder()
                .size(60)
                .minimalTemperature(0.0)
                .maximalTemperature(2.0)
                .warehouse(warehouse)
                .sectionProducts(new ArrayList<>(Arrays.asList(sectionProduct)))
                .inboundOrders(new ArrayList<>(Arrays.asList(inboundOrder)))
                .build();
        warehouse.setSections(new ArrayList<>(Arrays.asList(section)));
        sectionProduct.setSection(section);
        inboundOrder.setSection(section);
        return section;
    }

    private List<BatchStock> createValidBatchStocks() {
        return new ArrayList<>(Arrays.asList(
                BatchStock.builder()
                        .productId(1L)
                        .productSize(2.0)
                        .currentQuantity(10)
                        .dueDate(LocalDate.of(2022, 12, 02))
                        .build(),
                BatchStock.builder()
                        .productId(1L)
                        .productSize(2.0)
                        .currentQuantity(10)
                        .dueDate(LocalDate.of(2022, 12, 02))
                        .build(),
                BatchStock.builder()
                        .productId(1L)
                        .productSize(2.0)
                        .currentQuantity(10)
                        .dueDate(LocalDate.of(2022, 12, 02))
                        .build()
        ));
    }
}
