/*****************************************************************/
// File:   Attributes.java
// Author: Procesadores de Lenguajes-University of Zaragoza
// Date:   enero 2022
//         Clase única para almacenar los atributos que pueden aparecer en el traductor de adac
//         Se hace como clase plana, por simplicidad. Los atributos que se pueden almacenar
//         se autoexplican con el nombre
/*****************************************************************/

package lib.attributes;
import lib.symbolTable.*;

public class Attributes implements Cloneable {
    public Symbol.Types type;
    public Symbol.ParameterClass parClass;

    public int valInt;
    public boolean valBool;
    public char valChar;
    public String valString;
    public int tamanyo_vector;


    public Attributes() {
        type = Symbol.Types.UNDEFINED;
        parClass = Symbol.ParameterClass.NONE;
    }

    //Atributo entero no parametro
    public Attributes(int value){
        type = Symbol.Types.INT;
        valInt = value;
        parClass = Symbol.ParameterClass.NONE;
    }
    //Atributo entero parametro
    public Attributes(int value, Symbol.ParameterClass parameter){
        type = Symbol.Types.INT;
        valInt = value;
        parClass = parameter;
    }

    //Atributo char no parametro
    public Attributes(char value){
        type = Symbol.Types.CHAR;
        valChar = value;
        parClass = Symbol.ParameterClass.NONE;
    }
    //Atributo char parametro
    public Attributes(char value, Symbol.ParameterClass parameter){
        type = Symbol.Types.CHAR;
        valChar = value;
        parClass = parameter;
    }

    //Atributo bool no parametro
    public Attributes(boolean value){
        type = Symbol.Types.BOOL;
        valBool = value;
        parClass = Symbol.ParameterClass.NONE;
    }
    //Atributo bool parametro
    public Attributes(boolean value, Symbol.ParameterClass parameter){
        type = Symbol.Types.BOOL;
        valBool = value;
        parClass = parameter;
    }

    //Atributo string
    public Attributes(String value){
        type = Symbol.Types.STRING;
        valString = value;
        parClass = Symbol.ParameterClass.NONE;
    }

    //Atributo vector no parametro
    public Attributes(int tam, Symbol.Types vector_type){
        type = vector_type;
        tamanyo_vector=tam;
        parClass = Symbol.ParameterClass.NONE;
    }
    //Atributo vector parametro
    public Attributes(int tam, Symbol.Types vector_type,Symbol.ParameterClass parameter){
        type = vector_type;
        tamanyo_vector=tam;
        parClass = parameter;
    }



    public Attributes clone() {
    	try {
    		return (Attributes) super.clone();
    	}
    	catch (CloneNotSupportedException e) {
    		return null; 
    	}
    }

    public String toString() {
        return
            "type = " + type + "\n" +
            "parClass = " + parClass + "\n" ;
            //COMPLETAR
        
    }

    public static void comprobacion_tipo(Attributes atr_1, Attributes atr_2, boolean operador_booleano){
        if(operador_booleano){
            atr_1.comprobacion_tipo(Symbol.Types.BOOL);
            atr_2.comprobacion_tipo(Symbol.Types.BOOL);
        }else{//cualquier otro caso con los operadores aditivos o multiplicativos
            atr_1.comprobacion_tipo(Symbol.Types.INT);
            atr_2.comprobacion_tipo(Symbol.Types.INT);
        }
    }

    /* Comprobracion del tipo de dato|variable|expresion */
    public boolean comprobacion_tipo(Symbol.Types tipo_esperado){
        if(this.type != tipo_esperado){
            System.out.println("INCOMPATIBILIDAD DE TIPOS: Se esperaba un dato de tipo "+ tipo_esperado);
            this.type=tipo_esperado;
            return(false);
        }
        return(true);
    }


    /* Comprobacion del tipo en operadores relacionales */
    public void comprobacion_mismo_tipo(Attributes sim){
        if(this.type != sim.type){
            System.out.println("No se pueden comparar tipos distintos");
        }
    }

    public void comprobaciones_para_acciones_sin_parametros(Symbol accion){
        //TODO COMPROBAR QUE TIPO TIENE LA ACCION, Y SI TIENE LA LISTA DE PARAMETROS CON NOT EMPTY -> ERROR
    }

    public void comprobaciones_para_vectores(Symbol simbolo_del_factor){
        if(this.comprobacion_tipo(Symbol.Types.INT)){   //el indice tiene que ser un entero
            if(simbolo_del_factor != null && simbolo_del_factor instanceof SymbolArray) {   //el simbolo se tiene que poder transformar a un array
                if (((SymbolArray) simbolo_del_factor).maxInd > this.valInt && ((SymbolArray) simbolo_del_factor).minInd <= this.valInt) {  //el indice tiene que estar entre el tamaño del vector
                    this.type = ((SymbolArray) simbolo_del_factor).baseType;
                }else{
                    System.out.println("El indice tiene que ser menor que " + ((SymbolArray) simbolo_del_factor).maxInd + " y mayor o igual a "+((SymbolArray) simbolo_del_factor).minInd);
                    System.out.println("El indice recibido es " + this.valInt);
                }
            }
        }else{
            System.out.println("Para indexar un vector se necesita un valor entero");
        }
    }

    public void asignar_simbolo(Symbol simbolo_del_factor){
        this.type = simbolo_del_factor.type;
        Symbol auxiliar_parseo = simbolo_del_factor;
        if(simbolo_del_factor.type == Symbol.Types.INT){
            this.valInt = ((SymbolInt) auxiliar_parseo).value;
        }else if(simbolo_del_factor.type == Symbol.Types.BOOL){
            this.valBool = ((SymbolBool) auxiliar_parseo).value;
        }else if(simbolo_del_factor.type == Symbol.Types.CHAR){
            this.valChar = ((SymbolChar) auxiliar_parseo).value;
        }else if(simbolo_del_factor.type == Symbol.Types.STRING){
            this.valString = ((SymbolArray) auxiliar_parseo).toString();
            this.tamanyo_vector = ((SymbolArray) auxiliar_parseo).maxInd + 1;
        }else if(simbolo_del_factor.type == Symbol.Types.ARRAY){
            tamanyo_vector = ((SymbolArray) auxiliar_parseo).maxInd + 1;
        }else if(simbolo_del_factor.type == Symbol.Types.FUNCTION){
            System.out.println("No se puede utilizar una función sin sus parametros");
        }else if(simbolo_del_factor.type == Symbol.Types.PROCEDURE){
            System.out.println("No se puede utilizar un procedimiento sin sus parametros");
        }else if(simbolo_del_factor.type == Symbol.Types.UNDEFINED){
            System.out.println("El atributo pasa a ser un no definido"); //TODO WARNING
        }
    }

    public void es_parametro_de_accion(SymbolFunction simbolo_funcion, SymbolProcedure simbolo_procedimiento, int indice){
        if(simbolo_funcion != null){
            //utilizar simbolo_funcion
            Symbol.Types tipo_parametro = simbolo_funcion.parList.get(indice).type;
            if(this.type != tipo_parametro){
                System.out.println("El parametro con el que se esta invocando a la funcion es erroneo");
            }
        }else{
            //utilizar simbolo_procedimiento
            Symbol.Types tipo_parametro = simbolo_procedimiento.parList.get(indice).type;
            if(this.type != tipo_parametro){
                System.out.println("El parametro con el que se esta invocando al procedimiento es erroneo");
            }
        }
        //NO DEBERIA PODER SER AMBAS COSAS A LA VEZ
    }

}
