<%-- Vista: mantenimiento del catálogo de teléfonos celulares --%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core"%>
<%@taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Comercializadora Monster - Catálogo</title>
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body>
<%@include file="cabecera.jspf"%>
<main class="col-form">
    <section class="tarjeta">
        <h2>📱 Catálogo de teléfonos celulares</h2>
        <form method="get" action="catalogo" class="barra-busqueda">
            <input name="buscar" value="<c:out value='${buscar}'/>" placeholder="Buscar por marca o modelo">
            <label class="chk"><input type="checkbox" name="activos" ${activos ? 'checked' : ''}> Solo activos</label>
            <button class="btn" type="submit">Buscar</button>
        </form>
        <table>
            <thead><tr><th>Cód.</th><th>Marca</th><th>Modelo</th><th>Descripción</th><th class="der">Precio</th><th>Estado</th><th></th></tr></thead>
            <tbody>
            <c:forEach items="${celulares}" var="k" varStatus="st">
                <tr class="${celular.codigo == k.codigo ? 'sel' : ''}" style="animation-delay:${st.index * 0.03}s">
                    <td>${k.codigo}</td><td><c:out value="${k.marca}"/></td><td><c:out value="${k.modelo}"/></td>
                    <td><c:out value="${k.descripcion}"/></td>
                    <td class="der"><fmt:formatNumber value="${k.precio}" pattern="#,##0.00"/></td>
                    <td class="estado-${k.estado}">${k.estado}</td>
                    <td><a class="btn mini sec" href="catalogo?editar=${k.codigo}">Editar</a></td>
                </tr>
            </c:forEach>
            <c:if test="${empty celulares}"><tr><td colspan="7" class="vacio-hist">No hay celulares</td></tr></c:if>
            </tbody>
        </table>
    </section>
    <section class="tarjeta ${not empty error ? 'error' : ''}">
        <h2><c:choose><c:when test="${empty celular.codigo}">➕ Nuevo celular</c:when><c:otherwise>✏️ Editar celular N.º ${celular.codigo}</c:otherwise></c:choose></h2>
        <form method="post" action="catalogo" class="form-cli">
            <input type="hidden" name="codigo" value="${celular.codigo}">
            <div><label>Marca *</label><input name="marca" value="<c:out value='${celular.marca}'/>" placeholder="Ej.: Samsung" required></div>
            <div><label>Modelo *</label><input name="modelo" value="<c:out value='${celular.modelo}'/>" placeholder="Ej.: Galaxy A55" required></div>
            <div><label>Precio (USD) *</label><input name="precio" type="number" step="0.01" min="0.01"
                value="<c:if test='${not empty celular}'><fmt:formatNumber value='${celular.precio}' pattern='0.00' groupingUsed='false'/></c:if>" required></div>
            <div><label>Estado</label>
                <select name="estado">
                    <option ${celular.estado != 'INACTIVO' ? 'selected' : ''}>ACTIVO</option>
                    <option ${celular.estado == 'INACTIVO' ? 'selected' : ''}>INACTIVO</option>
                </select></div>
            <div class="full"><label>Descripción</label><textarea name="descripcion" rows="3"><c:out value="${celular.descripcion}"/></textarea></div>
            <div class="full acciones">
                <a class="btn sec" href="catalogo" style="text-align:center">Nuevo</a>
                <button class="btn verde" type="submit" name="accion" value="guardar">Guardar</button>
                <c:if test="${not empty celular.codigo}">
                    <button class="btn rojo" type="submit" name="accion" value="eliminar" formnovalidate
                            onclick="return confirm('¿Eliminar el celular N.º ${celular.codigo}?')">Eliminar</button>
                </c:if>
            </div>
        </form>
        <p style="color:var(--tenue);font-size:12px;margin-top:12px">Un celular ya facturado no se puede eliminar: cámbielo a INACTIVO.</p>
    </section>
</main>
<%@include file="pie.jspf"%>
</body>
</html>
