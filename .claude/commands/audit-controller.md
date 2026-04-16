---
description: Ejecuta auditoria de seguridad y revision de codigo sobre un Controller Spring y entrega un reporte consolidado para decision de PR.
argument-hint: [controller-path]
allowed-tools: Read, Task
---

# /audit-controller

Audita un Controller Spring usando dos subagentes y consolida hallazgos.

## Flujo obligatorio

1. Validacion de entrada
- Si no se recibe `controller-path`, pregunta al usuario que archivo quiere auditar y deten la ejecucion.

2. Lectura de archivo
- Lee el archivo del controller indicado.
- Si no existe o no es legible, responde error claro y deten la ejecucion.

3. Auditoria de seguridad
- Invoca al agente `java-security-auditor` con:
  - Ruta del archivo.
  - Contenido completo del controller.
- Guarda su salida textual como `security_report`.

4. Revision de codigo
- Invoca al agente `java-code-reviewer` con:
  - Ruta del archivo.
  - Contenido completo del controller.
- Guarda su salida textual como `code_review_report`.

5. Consolidacion
- Entrega una respuesta final con estas secciones exactas:

## Auditoria de Seguridad
[pegar `security_report`]

## Revision de Codigo
[pegar `code_review_report`]

6. Resumen ejecutivo
- Calcula cantidad de hallazgos por severidad para ambos reportes.
- Cierra con una recomendacion explicita:
  - "Listo para PR" cuando no hay hallazgos bloqueantes/criticos.
  - "Requiere correcciones antes de PR" cuando exista al menos un hallazgo bloqueante o critico.

## Criterios de calidad

- No modificar archivos del proyecto.
- No inventar lineas inexistentes.
- Ser explicito cuando falte contexto para concluir un riesgo.

