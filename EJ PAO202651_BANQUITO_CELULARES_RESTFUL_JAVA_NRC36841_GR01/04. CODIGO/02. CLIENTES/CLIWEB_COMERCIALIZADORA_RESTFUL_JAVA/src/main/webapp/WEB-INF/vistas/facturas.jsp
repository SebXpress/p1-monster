<%-- Vista: historial de facturas --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Comercializadora Monster - Facturas</title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
<%@include file="cabecera.jspf"%>
<main>
    <section class="tarjeta">
        <h2>📚 Facturas emitidas</h2>
        <form method="get" action="facturas" class="barra-busqueda">
            <input name="cedula" value="<c:out value='${cedula}'/>" placeholder="Cédula del cliente (vacío = todas)">
            <button class="btn" type="submit">Buscar</button>
        </form>
        <table>
            <thead><tr><th>N.º</th><th>Fecha</th><th>Cédula</th><th>Cliente</th><th>Forma de pago</th>
                <th class="der">Subtotal</th><th class="der">Descuento</th><th class="der">Total</th><th>Crédito</th><th></th></tr></thead>
            <tbody>
            <c:forEach items="${facturas}" var="f" varStatus="st">
                <tr style="animation-delay:${st.index * 0.03}s">
                    <td>${f.numero}</td><td>${f.fecha}</td><td>${f.cedula}</td><td><c:out value="${f.cliente}"/></td>
                    <td class="forma-${f.formaPago}"><span class="chip">${f.formaPago}</span></td>
                    <td class="der"><fmt:formatNumber value="${f.subtotal}" pattern="#,##0.00"/></td>
                    <td class="der"><fmt:formatNumber value="${f.descuento}" pattern="#,##0.00"/></td>
                    <td class="der"><b><fmt:formatNumber value="${f.total}" pattern="#,##0.00"/></b></td>
                    <td><c:if test="${f.formaPago == 'CREDITO'}">N.º ${f.codigoCredito} · ${f.numeroCuotas} x <fmt:formatNumber value="${f.valorCuota}" pattern="#,##0.00"/></c:if></td>
                    <td><a class="btn mini sec" href="factura?numero=${f.numero}">Ver</a></td>
                </tr>
            </c:forEach>
            <c:if test="${empty facturas}"><tr><td colspan="10" class="vacio-hist">No hay facturas</td></tr></c:if>
            </tbody>
        </table>
    </section>
</main>
<%@include file="pie.jspf"%>
</body>
</html>
