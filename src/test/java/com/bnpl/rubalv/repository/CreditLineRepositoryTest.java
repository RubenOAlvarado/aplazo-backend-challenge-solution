package com.bnpl.rubalv.repository;

public class CreditLineRepository {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private CreditLineRepository creditLineRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;
    private CreditLine activeCreditLine;
    private CreditLine inactiveCreditLine;

    @BeforeEach
    void setUp() {
        creditLineRepository.deleteAll();
        customerRepository.deleteAll();

        // Create test customer
        customer = new Customer();
        customer.setFirstName("Test");
        customer.setLastName("User");
        customer.setSecondLastName("Repository");
        customer.setDateOfBirth(LocalDate.now().minusYears(30));
        customer = customerRepository.save(customer);

        // Create active credit line
        activeCreditLine = new CreditLine();
        activeCreditLine.setCustomer(customer);
        activeCreditLine.setTotalAmount(new BigDecimal("5000.00"));
        activeCreditLine.setAvailableAmount(new BigDecimal("5000.00"));
        activeCreditLine.setActive(true);
        activeCreditLine.setCreatedAt(LocalDateTime.now());
        activeCreditLine = creditLineRepository.save(activeCreditLine);

        // Create inactive credit line
        inactiveCreditLine = new CreditLine();
        inactiveCreditLine.setCustomer(customer);
        inactiveCreditLine.setTotalAmount(new BigDecimal("2000.00"));
        inactiveCreditLine.setAvailableAmount(new BigDecimal("0.00"));
        inactiveCreditLine.setActive(false);
        inactiveCreditLine.setCreatedAt(LocalDateTime.now().minusDays(30));
        inactiveCreditLine = creditLineRepository.save(inactiveCreditLine);
    }

    @Test
    void shouldFindActiveByCustomer() {
        // Act
        Optional<CreditLine> result = creditLineRepository.findActiveByCustomer(customer);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(activeCreditLine.getId(), result.get().getId());
        assertEquals(new BigDecimal("5000.00"), result.get().getTotalAmount());
        assertTrue(result.get().isActive());
    }

    @Test
    void shouldNotFindActiveWhenCustomerHasNoActiveCreditLine() {
        // Arrange
        activeCreditLine.setActive(false);
        creditLineRepository.save(activeCreditLine);

        // Act
        Optional<CreditLine> result = creditLineRepository.findActiveByCustomer(customer);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void shouldNotFindActiveWhenCustomerDoesNotExist() {
        // Arrange
        Customer nonExistentCustomer = new Customer();
        nonExistentCustomer.setId(UUID.randomUUID());
        nonExistentCustomer.setFirstName("Non");
        nonExistentCustomer.setLastName("Existent");

        // Act
        Optional<CreditLine> result = creditLineRepository.findActiveByCustomer(nonExistentCustomer);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindActiveCreditLineAmongMultipleCreditLines() {
        // Arrange
        CreditLine secondActiveCreditLine = new CreditLine();
        secondActiveCreditLine.setCustomer(customer);
        secondActiveCreditLine.setTotalAmount(new BigDecimal("10000.00"));
        secondActiveCreditLine.setAvailableAmount(new BigDecimal("7500.00"));
        secondActiveCreditLine.setActive(true);
        secondActiveCreditLine.setCreatedAt(LocalDateTime.now().plusDays(1)); // More recent
        creditLineRepository.save(secondActiveCreditLine);

        // Act
        Optional<CreditLine> result = creditLineRepository.findActiveByCustomer(customer);

        // Assert
        assertTrue(result.isPresent());

        // Note: If you expect to return the most recent one, uncomment below:
        // assertEquals(secondActiveCreditLine.getId(), result.get().getId());

        // If you just expect any active credit line, use this:
        assertTrue(result.get().isActive());
        assertThat(result.get().getId()).isIn(activeCreditLine.getId(), secondActiveCreditLine.getId());
    }

    @Test
    void shouldReturnEmptyForDifferentCustomer() {
        // Arrange
        Customer anotherCustomer = new Customer();
        anotherCustomer.setFirstName("Another");
        anotherCustomer.setLastName("User");
        anotherCustomer.setDateOfBirth(LocalDate.now().minusYears(25));
        anotherCustomer = customerRepository.save(anotherCustomer);

        // Act
        Optional<CreditLine> result = creditLineRepository.findActiveByCustomer(anotherCustomer);

        // Assert
        assertFalse(result.isPresent());
    }
}
