# Lib Exceptions

Biblioteca responsável por padronizar o tratamento e resposta de exceções no ecossistema **Tap Track System**.

## Recursos

- `ErrorResponse` (record padronizado)
- `GlobalExceptionHandler` com integração Spring
- Exceções customizadas (Conflict, NotFound, Unprocessable, Internal)
- Log estruturado (`Slf4j`)

## Instalação

```xml

<dependency>
    <groupId>com.taptrack.lib</groupId>
    <artifactId>lib-exceptions</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Uso

Basta importar o pacote:

```java
import com.taptrack.lib.exceptions.handler.GlobalExceptionHandler;
```

E lançar exceções personalizadas em qualquer serviço:

```java
throw new ResourceNotFoundException("Usuário não encontrado.");
```