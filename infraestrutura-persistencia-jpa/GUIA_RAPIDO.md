# 🚗 Guia Rápido - Camada de Persistência JPA

## ⚡ Início Rápido

### 1. Adicione a Dependência

No `pom.xml` do seu módulo de aplicação:

```xml
<dependency>
    <groupId>dev.sauloaraujo.alugacar</groupId>
    <artifactId>alugacar-infraestrutura-persistencia-jpa</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### 2. Configure o Spring Boot

```java
@SpringBootApplication
@EnableJpaRepositories(basePackages = "dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa")
@EntityScan(basePackages = "dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities")
public class AlugaCarApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlugaCarApplication.class, args);
    }
}
```

### 3. Injete e Use os Repositórios

```java
@Service
public class VeiculoService {
    
    @Autowired
    private VeiculoRepositorio veiculoRepositorio; // Interface do domínio
    
    @Autowired
    private CategoriaRepositorio categoriaRepositorio;
    
    public List<Veiculo> listarVeiculosDisponiveis(String cidade, CategoriaCodigo categoria) {
        return veiculoRepositorio.buscarDisponiveis(cidade, categoria);
    }
    
    public void cadastrarVeiculo(Veiculo veiculo) {
        veiculoRepositorio.salvar(veiculo);
    }
}
```

## 📚 Exemplos de Uso

### Cadastrar um Novo Veículo

```java
var veiculo = new Veiculo(
    "ABC1234",                      // placa
    "Volkswagen Gol 1.0",           // modelo
    CategoriaCodigo.ECONOMICO,      // categoria
    "São Paulo",                    // cidade
    new BigDecimal("89.90"),        // diária
    StatusVeiculo.DISPONIVEL        // status
);

veiculoRepositorio.salvar(veiculo);
```

### Buscar Veículo por Placa

```java
Optional<Veiculo> veiculo = veiculoRepositorio.buscarPorPlaca("ABC1234");

if (veiculo.isPresent()) {
    System.out.println("Modelo: " + veiculo.get().getModelo());
    System.out.println("Disponível: " + veiculo.get().disponivel());
}
```

### Listar Veículos Disponíveis

```java
// Todos os veículos disponíveis em uma cidade
List<Veiculo> todosVeiculos = veiculoRepositorio.buscarDisponiveis("Rio de Janeiro");

// Veículos de uma categoria específica
List<Veiculo> economicos = veiculoRepositorio.buscarDisponiveis(
    "Rio de Janeiro", 
    CategoriaCodigo.ECONOMICO
);
```

### Cadastrar Cliente

```java
var cliente = new Cliente(
    "João Silva",           // nome
    "12345678901",          // CPF/CNPJ
    "98765432100",          // CNH
    "joao@email.com"        // email
);

clienteRepositorio.salvar(cliente);
```

### Criar Reserva

```java
var cliente = clienteRepositorio.buscarPorDocumento("12345678901").get();

var periodo = new PeriodoLocacao(
    LocalDateTime.now().plusDays(1),    // retirada
    LocalDateTime.now().plusDays(5)     // devolução
);

var reserva = new Reserva(
    CategoriaCodigo.ECONOMICO,
    "São Paulo",                        // cidade retirada
    periodo,
    new BigDecimal("449.50"),           // valor estimado
    cliente
);

reservaRepositorio.salvar(reserva);
```

### Buscar Reserva e Criar Locação

```java
var reserva = reservaRepositorio.buscarPorCodigo("uuid-da-reserva").get();
var veiculo = veiculoRepositorio.buscarPorPlaca("ABC1234").get();

var vistoria = new ChecklistVistoria(
    50000,              // quilometragem
    "Cheio",            // combustível
    false               // possui avarias
);

var locacao = new Locacao(
    UUID.randomUUID().toString(),   // código
    reserva,
    veiculo,
    5,                              // dias previstos
    new BigDecimal("89.90"),        // valor diária
    vistoria                        // vistoria retirada
);

locacaoRepositorio.salvar(locacao);
```

## 🗄️ Banco de Dados

### Console H2 (Desenvolvimento)

Acesse: `http://localhost:8080/h2-console`

**Configurações**:
- JDBC URL: `jdbc:h2:mem:alugacar`
- Username: `sa`
- Password: _(vazio)_

### Dados Iniciais (Seed)

O Flyway carrega automaticamente:
- **5 categorias** (Econômico, Compacto, Intermediário, SUV, Luxo)
- **3 clientes** de exemplo
- **14 veículos** disponíveis (São Paulo e Rio de Janeiro)

### Consultas SQL Úteis

```sql
-- Ver todos os veículos disponíveis
SELECT * FROM VEICULO WHERE status = 'DISPONIVEL';

-- Ver veículos por cidade
SELECT * FROM VEICULO WHERE cidade = 'São Paulo';

-- Ver categorias e suas diárias
SELECT codigo, nome, diaria FROM CATEGORIA ORDER BY diaria;

-- Ver reservas ativas
SELECT * FROM RESERVA WHERE status = 'ATIVA';

-- Ver locações em andamento
SELECT * FROM LOCACAO WHERE status = 'ATIVA';
```

## 🔍 Troubleshooting

### Erro: "Table not found"

**Solução**: Verifique se o Flyway está habilitado no `application.properties`:

```properties
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
```

### Erro: "No qualifying bean of type"

**Solução**: Adicione o scan dos pacotes JPA:

```java
@EnableJpaRepositories(basePackages = "dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa")
@EntityScan(basePackages = "dev.sauloaraujo.alugacar.infraestrutura.persistencia.jpa.entities")
```

### Erro ao converter entidades

**Solução**: Certifique-se de que o `ModelMapper` está configurado como Bean:

```java
@Configuration
public class JpaConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
```

## 📊 Estrutura de Dados

### Relacionamentos

```
CATEGORIA ──┐
            │
            ├──→ VEICULO
            │
            └──→ RESERVA ──→ LOCACAO
                   │
                   └──→ CLIENTE
```

### Campos Principais

**VEICULO**
- `placa` (PK)
- `modelo`, `categoria`, `cidade`
- `diaria`, `status`
- `patio_codigo`, `patio_localizacao`

**CLIENTE**
- `cpf_cnpj` (PK)
- `nome`, `cnh`, `email`

**RESERVA**
- `codigo` (PK)
- `categoria`, `cidade_retirada`
- `data_retirada`, `data_devolucao`
- `valor_estimado`, `status`
- `cliente_cpf_cnpj` (FK)

**LOCACAO**
- `codigo` (PK)
- `reserva_codigo` (FK)
- `veiculo_placa` (FK)
- `dias_previstos`, `valor_diaria`
- `quilometragem`, `combustivel`, `possui_avarias`
- `status`

## 🧪 Executar Testes

```bash
cd infraestrutura-persistencia-jpa
mvn test
```

## 📖 Documentação Completa

Consulte o `README.md` para documentação detalhada da arquitetura.

---

**Dica**: Todos os repositórios retornam objetos de **domínio**, não entidades JPA. A conversão é feita automaticamente! 🎯
