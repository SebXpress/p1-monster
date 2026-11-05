<%-- Vista: facturación (efectivo con 42 % de descuento o crédito directo del Banco BanQuito) --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Comercializadora Monster - Facturar</title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
<%@include file="cabecera.jspf"%>
<form method="post" action="facturar" id="venta">
<main class="col-form">
    <section class="tarjeta">
        <h2>🧾 Teléfonos de la venta</h2>
        <div class="fila-agregar">
            <div><label>Teléfono celular</label>
                <select name="codigoCelular">
                    <c:forEach items="${celulares}" var="k">
                        <option value="${k.codigo}"><c:out value="${k.marca} ${k.modelo}"/> — $<fmt:formatNumber value="${k.precio}" pattern="#,##0.00"/></option>
                    </c:forEach>
                </select></div>
            <div><label>Cantidad</label><input type="number" name="cantidad" value="1" min="1" max="100"></div>
            <button class="btn azul" type="submit" name="accion" value="agregar" formnovalidate>Agregar</button>
        </div>
        <table>
            <thead><tr><th>Cód.</th><th>Celular</th><th class="der">Cantidad</th><th class="der">P. Unitario</th><th class="der">Subtotal</th><th></th></tr></thead>
            <tbody>
            <c:forEach items="${lineas}" var="l">
                <tr><td>${l.codigo}</td><td><c:out value="${l.celular}"/></td><td class="der">${l.cantidad}</td>
                    <td class="der"><fmt:formatNumber value="${l.precio}" pattern="#,##0.00"/></td>
                    <td class="der"><fmt:formatNumber value="${l.subtotal}" pattern="#,##0.00"/></td>
                    <td><button class="btn mini rojo" type="submit" name="accion" value="quitar:${l.codigo}" formnovalidate>Quitar</button></td></tr>
            </c:forEach>
            <c:if test="${empty lineas}"><tr><td colspan="6" class="vacio-hist">Agregue teléfonos a la venta</td></tr></c:if>
            </tbody>
        </table>
        <dl class="totales">
            <dt>Subtotal</dt><dd>$ <fmt:formatNumber value="${subtotal}" pattern="#,##0.00"/></dd>
            <dt>Descuento ${venta.formaPago == 'CREDITO' ? '' : '(efectivo 42 %)'}</dt><dd class="ing">- $ <fmt:formatNumber value="${descuento}" pattern="#,##0.00"/></dd>
            <dt style="align-self:center">TOTAL</dt><dd class="gran">$ <span class="contar" data-valor="${total}"><fmt:formatNumber value="${total}" pattern="#,##0.00"/></span></dd>
            <c:if test="${venta.formaPago == 'CREDITO' and subtotal > 0}">
                <dt></dt><dd class="cuota">${venta.numeroCuotas} cuotas estimadas de $ <fmt:formatNumber value="${cuotaEstimada}" pattern="#,##0.00"/></dd>
            </c:if>
        </dl>
    </section>

    <div style="display:grid;gap:22px">
        <section class="tarjeta">
            <h2>👤 Comprador</h2>
            <div class="form-cli">
                <div><label>Cédula *</label><input name="cedula" value="<c:out value='${venta.cedula}'/>" maxlength="10" placeholder="10 dígitos"></div>
                <div><label>Teléfono</label><input name="telefono" value="<c:out value='${venta.telefono}'/>"></div>
                <div class="full"><label>Nombre *</label><input name="nombre" value="<c:out value='${venta.nombre}'/>" placeholder="Nombres y apellidos"></div>
                <div class="full"><label>Dirección</label><input name="direccion" value="<c:out value='${venta.direccion}'/>"></div>
                <div class="full"><label>Correo</label><input name="correo" type="email" value="<c:out value='${venta.correo}'/>"></div>
            </div>
        </section>
        <section class="tarjeta" id="pago">
            <h2>💳 Forma de pago</h2>
            <div class="pago">
                <label class="op op-ef"><input type="radio" name="formaPago" value="EFECTIVO" ${venta.formaPago != 'CREDITO' ? 'checked' : ''}
                       onchange="this.form.submit()"> 💵 Efectivo — 42 % de descuento</label>
                <label class="op op-cr"><input type="radio" name="formaPago" value="CREDITO" ${venta.formaPago == 'CREDITO' ? 'checked' : ''}
                       onchange="this.form.submit()"> 🏦 Crédito directo — Banco BanQuito</label>
                <c:if test="${venta.formaPago == 'CREDITO'}">
                    <div class="credito-fila">
                        <div><label>Cuotas (3 a 18)</label>
                            <input type="number" name="numeroCuotas" min="3" max="18" value="${venta.numeroCuotas}"
                                   onchange="this.form.submit()"></div>
                        <button class="btn cian" type="submit" name="accion" value="evaluar">Consultar al banco</button>
                    </div>
                    <c:if test="${not empty sessionScope.evaluacion}">
                        <c:set var="ev" value="${sessionScope.evaluacion}"/>
                        <div class="eval ${ev.sujetoCredito and ev.alcanza ? 'si' : 'no'}">
                            <c:choose>
                                <c:when test="${not ev.esCliente}"><b>No es cliente del Banco BanQuito.</b></c:when>
                                <c:otherwise>
                                    <b><c:out value="${ev.nombre}"/></b><br>
                                    <c:choose>
                                        <c:when test="${ev.sujetoCredito}">
                                            ✔ Sujeto de crédito · Monto máximo: <b>$ <fmt:formatNumber value="${ev.montoMaximo}" pattern="#,##0.00"/></b><br>
                                            ${ev.alcanza ? '✔ El valor de la compra está aprobado.' : '✖ El valor de la compra supera el monto máximo.'}
                                        </c:when>
                                        <c:otherwise>✖ No es sujeto de crédito:</c:otherwise>
                                    </c:choose>
                                </c:otherwise>
                            </c:choose>
                            <c:forEach items="${ev.motivos}" var="m"><br>• <c:out value="${m}"/></c:forEach>
                        </div>
                    </c:if>
                </c:if>
            </div>
            <div class="acciones">
                <button class="btn sec" type="submit" name="accion" value="limpiar" formnovalidate>Limpiar</button>
                <button class="btn verde" type="submit" name="accion" value="facturar"
                        onclick="return confirm('¿Emitir la factura?')">FACTURAR</button>
            </div>
        </section>
    </div>
</main>
</form>
<%@include file="pie.jspf"%>
</body>
</html>
