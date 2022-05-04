package br.com.group9.pimlwarehouse.service;

import br.com.group9.pimlwarehouse.repository.SectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SectionServiceTests {
    private SectionService sectionService;
    private SectionRepository sectionRepositoryMock;
    private SectionProductService sectionProductServiceMock;
    private ProductAPIService productAPIServiceMock;

    @BeforeEach
    public void setupService() {
        sectionRepositoryMock = Mockito.mock(SectionRepository.class);
        sectionProductServiceMock = Mockito.mock(SectionProductService.class);
        productAPIServiceMock = Mockito.mock(ProductAPIService.class);
        sectionService = new SectionService(sectionRepositoryMock, sectionProductServiceMock, productAPIServiceMock);
    }


    /**
     * Métodos de SectionService:
     *  - public Section findById(Long id)
     *  - public void validateBatchStocksBySection(Long sectorId, Long warehouseId, List<BatchStock> batchStocks)
     *  - public Section associateProductToSectionByIds(Long sectionId, Long productId)
     */

    /**
     *  - public Section findById(Long id)
     *      Cenários:
     *          -Section encontrada com sucesso.
     *          -Section não é encontrada (lança exceção)
     */

    @Test
    public void shouldFindWhenSectionIsFound() {
    }

    @Test
    public void shouldThrowExceptionWhenSectionNotFound() {

    }

    /**
     *  - public void validateBatchStocksBySection(Long sectorId, Long warehouseId, List<BatchStock> batchStocks)
     *      Cenários:
     *          -Valida Section com sucesso.
     *          -Section não está associado a Warehouse (lança exceção).
     *          -Produto do BatchStock não está associado a Section (lança exceção).
     *          -Section não possui espaço o suficiente (lança exceção).
     */

    @Test
    public void shouldSuccessfullyValidateWhenValidSection() {

    }

    @Test
    public void shouldThrowExceptionWhenWarehouseDoesNotMatchSection() {

    }

    @Test
    public void shouldThrowExceptionWhenProductDoesNotMatchSection() {

    }

    @Test
    public void shouldThrowExceptionWhenSectionSpaceNotEnough() {

    }

    /**
     *  - public Section associateProductToSectionByIds(Long sectionId, Long productId)
     *      Cenários:
     *          -Associa produto à Section com sucesso.
     *          -Temperatura mínima do produto é maior que máxima da Section (lança exceção).
     *          -Temperatura mínima do produto é menor que mínima da Section (lança exceção).
     *          -Associação já foi criada anteriormente (lança exceção).
     */

}
