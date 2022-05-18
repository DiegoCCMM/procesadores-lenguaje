AUTORES
Diego Caballé Casanova 738712
Alicia Yasmina Albero Escudero	721288
****************************************************************************
Práctica 1: Construcción de un analizador léxico para "adac"
****************************************************************************

Detectamos todas las palabras claves del lenguaje adac como son los comentarios con doble guión, la declaración de
variables integer character y boolean, vectores, if y while, funciones y procedimientos con parametros pasados por valor
 y referencia, las operaciones put, get, put_line y get_line, las operaciones matemáticas básicas y el paso de entero a
 caracter y viceversa.

****************************************************************************
Práctica 2: Construcción del analizador sintáctico para "adac"
****************************************************************************

Aplicamos la sintáxis especifica a las operaciones if while, la prioridad de las operaciones está declarada implicitamente
 en la gramática y la definición de funciones y métodos.

****************************************************************************
Práctica 3: Construcción del analizador semántico para "adac"
****************************************************************************
Nivel 4 - El lenguaje permite el uso de parametros escalares y de vectores, tanto por
valor como por referencia en procedimientos y funciones

Se amplió la clase Attributes con distintos constructores y métodos para reducir las líneas de código en adac.jj
Estos métodos incluyen comprabaciones para asignaciones, accesos a vectores, lecturas e invocaciones a acciones entre
otras.

--Consideraciones semánticas--
Declaraciones
  -El tamaño del vector debe ser >0
Funciones
  - Debe existir una sentencia return
  - El tipo de la sentencia return es el mismo que el de la funcion
Procedimientos
  - No se usa return
Get
  - No se pueden leer vectores completos, funciones ni procedimientos
Put/Put_line
  - No se pueden escribir vectores completos, funciones ni procedimientos
Bucle
  - La condicion es una expresión booleana
Seleccion
  - La condicion es una expresión booleana
Asignacion
  - El identificador de la izquierda debe ser asignable (variable o posicion de vector)
  - Los tipos entre el asignable y expresion deben coincidir
Expresiones/factores
  - Los operandos deben ser del mismo tipo (comparaciones)
  - Los operandos deben ser operables ( operaciones con enteros)
  - Los identificadores deben existir en la tabla de simbolos
LLamada a acciones
  - Los operandos pasados como parametros han de estar en el mismo orden y ser del mismo tipo que en la declaracion
  - El número de parametros tiene que ser correcto
Vectores
  - La expresion utilizada para indexar tiene que ser de tipo entero
