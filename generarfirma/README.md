# Generación de la firma digital

Este directorio contiene la configuración y los archivos necesarios para generar `sife.p12`, el almacén PKCS#12 usado por el backend para firmar el XML de la factura electrónica.

La clave del PKCS#12 es `123qwe`.

## Archivos

| Archivo | Descripción |
| --- | --- |
| `configuracion.txt` | Configuración OpenSSL con el `distinguished name` y los OID del certificado. |
| `sife.pem` | Llave privada en formato PEM. |
| `sife.csr` | Solicitud de firma de certificado. |
| `sife.crt` | Certificado de prueba autofirmado. |
| `sife.p12` | Almacén PKCS#12 que se configura en `empresa.ruta_firma_digital`. |

## 1. Preparar `configuracion.txt`

Editar los valores antes de generar la llave:

```text
[ dn ]
CN = SIFE
O = Razon Social
OU = unidad organizacional
title = cargo del representante
C = BO
dnQualifier = NIT
uidNumber = 710035027
uid = 20
serialNumber = 710035027
emailAddress = fatvalsrl@gmail.com
```

Significado de los campos:

| Campo | Uso |
| --- | --- |
| `CN` | Nombre común del certificado. |
| `O` | Razón social del contribuyente. |
| `OU` | Unidad organizacional. |
| `title` | Cargo del representante legal. |
| `C` | País, `BO` para Bolivia. |
| `dnQualifier` | Valor del NIT. |
| `uidNumber` | OID `1.3.6.1.1.1.1.0`; para este proyecto usa `710035027`. |
| `uid` | Identificador interno del emisor. |
| `serialNumber` | NIT del contribuyente. |
| `emailAddress` | Correo de contacto. |

Los valores `default_bits = 2048`, `prompt = no`, `encrypt_key = no` y `default_md = sha1` se toman directamente de `configuracion.txt`.

## 2. Ubicarse en el directorio

```bash
cd generarfirma
```

## 3. Generar la llave privada y el CSR

```bash
openssl req -new -config configuracion.txt -keyout sife.pem -out sife.csr
```

Verificar el CSR:

```bash
openssl req -in sife.csr -noout -text
openssl req -in sife.csr -noout -verify
```

## 4. Generar el certificado de prueba

Para desarrollo se usa un certificado autofirmado:

```bash
openssl x509 -req -in sife.csr -signkey sife.pem -out sife.crt -days 3650 -sha256
```

Verificar el certificado:

```bash
openssl x509 -in sife.crt -noout -text
openssl x509 -in sife.crt -noout -subject -issuer -dates
```

Para producción, reemplazar `sife.crt` por el certificado emitido por una CA autorizada y conservar la cadena de certificados en un archivo, por ejemplo `cadena.pem`.

## 5. Generar `sife.p12`

Con el certificado autofirmado de desarrollo:

```bash
openssl pkcs12 -export \
  -inkey sife.pem \
  -in sife.crt \
  -name sife \
  -out sife.p12 \
  -passout pass:123qwe
```

Si se usa un certificado emitido por una CA y se cuenta con la cadena intermedia:

```bash
openssl pkcs12 -export \
  -inkey sife.pem \
  -in sife.crt \
  -certfile cadena.pem \
  -name sife \
  -out sife.p12 \
  -passout pass:123qwe
```

Si Java indica que el formato PKCS#12 es demasiado nuevo, usar el modo legacy:

```bash
openssl pkcs12 -export -legacy \
  -inkey sife.pem \
  -in sife.crt \
  -name sife \
  -out sife.p12 \
  -passout pass:123qwe
```

## 6. Verificar el PKCS#12

```bash
openssl pkcs12 -in sife.p12 -info -noout -passin pass:123qwe
```

También se puede verificar con Java:

```bash
keytool -list -v -storetype PKCS12 -keystore sife.p12 -storepass 123qwe
```

La salida debe mostrar el alias `sife`, la llave privada y el certificado.

## 7. Proteger los archivos

```bash
chmod 600 sife.pem sife.p12
chmod 644 configuracion.txt sife.csr sife.crt
```

No versionar `sife.p12` ni `sife.pem`. Si el repositorio no los ignora, agregar a `.gitignore`:

```text
generarfirma/sife.pem
generarfirma/sife.p12
```

## 8. Configurar el backend

La empresa debe apuntar al archivo generado:

| Campo | Valor |
| --- | --- |
| `ruta_firma_digital` | `/home/cesarnvf/Capacitacion/sife/generarfirma/sife.p12` |
| `clave_firma_digital` | `123qwe` |

Ejemplo:

```sql
UPDATE empresa
SET ruta_firma_digital = '/home/cesarnvf/Capacitacion/sife/generarfirma/sife.p12',
    clave_firma_digital = '123qwe'
WHERE id = 1;
```

## Secuencia completa

```bash
cd generarfirma
openssl req -new -config configuracion.txt -keyout sife.pem -out sife.csr
openssl req -in sife.csr -noout -text
openssl x509 -req -in sife.csr -signkey sife.pem -out sife.crt -days 3650 -sha256
openssl x509 -in sife.crt -noout -subject -issuer -dates
openssl pkcs12 -export -inkey sife.pem -in sife.crt -name sife -out sife.p12 -passout pass:123qwe
openssl pkcs12 -in sife.p12 -info -noout -passin pass:123qwe
keytool -list -v -storetype PKCS12 -keystore sife.p12 -storepass 123qwe
chmod 600 sife.pem sife.p12
```
