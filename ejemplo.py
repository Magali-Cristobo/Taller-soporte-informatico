import pandas as pd
df = pd.read_csv("datos_nomivac_covid19.csv")
print(df)
print(list(df.columns))
print(list(df.index))
print(df['grupo_etario']) #obtener todos los datos de la columna
df.loc[0:1000] # las primeras 1000 filas, el rango. el loc
df.loc[[1,5,8]]
dfConIndiceDNI = df.set_index('DNI')
dfSinFilasConNaN = df.dropna()
df['sexo'].value_counts() # recorre toda la columna busca cuantos valores distintos de cada cosa hay
df_2 = df.drop_duplicates() # borra duplicados
df.dropna(inplace=True) # los cambios se hacen en df, no retorna una instancia
df.to_csv("nuevo.csv")