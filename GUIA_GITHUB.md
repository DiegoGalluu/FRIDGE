# Guia de GitHub

Esta guia resume una forma sencilla de publicar y mantener el proyecto FRIDGE en GitHub.

## Crear el repositorio

Crea un repositorio llamado:

```text
FRIDGE
```

Puede ser publico o privado, segun el uso que quieras darle.

## Primer commit

Desde la carpeta del proyecto:

```bash
git init
git add .
git commit -m "creacion inicial de la app"
```

Despues conecta el remoto:

```bash
git remote add origin URL_DEL_REPOSITORIO
git branch -M main
git push -u origin main
```

## Mantenimiento

Una secuencia clara de commits facilita seguir la evolucion del proyecto:

```bash
git add .
git commit -m "ajustada interfaz de despensa"
```

```bash
git add .
git commit -m "mejorada busqueda de recetas"
```

```bash
git add .
git commit -m "actualizado informe de despensa"
```

## Archivos utiles

- `README.md`: descripcion del proyecto y guia de uso.
- `RESULTADOS.md`: ejemplos de salidas e informes.
- `app/src/main/java/com/example/fridge`: codigo principal de la aplicacion.
