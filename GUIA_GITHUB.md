# GUIA GITHUB

El enunciado de PSP indica que el profesor puede pedir repositorio publico con commits.

Esta guia explica como subir el proyecto FRIDGE a GitHub.

## Crear repositorio

Crea en GitHub un repositorio publico llamado:

```text
FRIDGE
```

No hace falta que la app cree el repositorio automaticamente.

## Comandos recomendados

Desde la carpeta del proyecto ejecuta:

```bash
git init
git add .
git commit -m "creacion inicial de la app"
```

Despues crea el repositorio en GitHub y conecta el remoto:

```bash
git remote add origin URL_DEL_REPOSITORIO
git branch -M main
git push -u origin main
```

## Commits sugeridos para demostrar progreso

Puedes hacer los commits de esta forma:

```bash
git add .
git commit -m "creacion inicial de la app"
```

```bash
git add .
git commit -m "anadida interfaz con compose"
```

```bash
git add .
git commit -m "anadidos procesos con processbuilder"
```

```bash
git add .
git commit -m "anadidos hilos corrutinas y documentacion"
```

## Que entregar

Entrega al profesor:

- enlace del repositorio publico
- proyecto Android completo
- README.md
- RESULTADOS.md
- explicacion oral de la app funcionando
