<%-- Vista: consulta de la tabla de amortización por cédula --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Comercializadora Monster - Tabla de amortización</title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
<%@include file="cabecera.jspf"%>
<main class="col-form" style="grid-template-columns:1fr 1.6fr">
    <section class="tarjeta">
        <h2>🔎 Créditos del cliente</h2>
        <form method="get" action="amortizacion" class="barra-busqueda">
            <input name="cedula" value="<c:out value='${cedula}'/>" maxlength="10" placeholder="Cédula del cliente" required>
            <button class="btn" type="submit">Consultar</button>
        </form>
        <table>
            <thead><tr><th>Crédito</th><th>Fecha</th><th class="der">Monto</th><th class="der">Cuotas</th><th class="der">Cuota</th><th>Estado</th></tr></thead>
            <tbody>
            <c:forEach items="${creditos}" var="k">
                <tr class="${credito.codigoCredito == k.codigoCredito ? 'sel' : ''}">
                    <td><a class="fila-link" href="amortizacion?cedula=${cedula}&credito=${k.codigoCredito}">N.º ${k.codigoCredito}</a></td>
                    <td>${k.fecha}</td>
                    <td class="der"><fmt:formatNumber value="${k.monto}" pattern="#,##0.00"/></td>
                    <td class="der">${k.numeroCuotas}</td>
                    <td class="der"><fmt:formatNumber value="${k.cuota}" pattern="#,##0.00"/></td>
                    <td class="estado-${k.estado}">${k.estado}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty creditos}"><tr><td colspan="6" class="vacio-hist">Ingrese la cédula para ver sus créditos</td></tr></c:if>
            </tbody>
        </table>
    </section>
    <section class="tarjeta">
        <c:choose>
            <c:when test="${not empty credito}"><%@include file="tabla_amortizacion.jspf"%></c:when>
            <c:otherwise><div class="comprobante vacio">📊 La tabla de amortización aparecerá aquí<br><small>Cuota fija mensual · 16.5 % anual · 3 a 18 meses</small></div></c:otherwise>
        </c:choose>
    </section>
</main>
<%@include file="pie.jspf"%>
</body>
</html>
