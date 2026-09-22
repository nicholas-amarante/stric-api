# Tesseract OCR - Arquivos de Treinamento

Para o OCR funcionar corretamente, faça o download dos seguintes arquivos de treinamento:

## Download Automático (execute no terminal na raiz do projeto):

```bash
# Português (Brasil)
curl -L -o tessdata/por.traineddata https://github.com/tesseract-ocr/tessdata/raw/main/por.traineddata

# Inglês
curl -L -o tessdata/eng.traineddata https://github.com/tesseract-ocr/tessdata/raw/main/eng.traineddata
```

## Download Manual:

1. Acesse: https://github.com/tesseract-ocr/tessdata
2. Baixe `por.traineddata` e `eng.traineddata`
3. Coloque os arquivos nesta pasta `tessdata/`

## Observação:

Os arquivos `.traineddata` são grandes (~10-30MB cada) e não devem ser commitados no Git.
