import pandas as pd
df = pd.read_csv("estaciones-de-subte.csv")
# dfConIndiceNombre = df.set_index('latitud') # mi csv no tiene el nombre de la columna, pero si lo tuviera, lo haria asi
dfSinFilasDuplicadas = df.drop_duplicates()
dfSinFilasConNaN = dfSinFilasDuplicadas.dropna()
dfSinFilasConNaN.to_csv("nuevo.csv")
print(dfSinFilasConNaN.loc[0:3000])
print(dfSinFilasConNaN.loc[len(dfSinFilasConNaN.index)-2000:len(dfSinFilasConNaN.index)-1])
# funciona pero la primera fila me la muestra de una forma rara