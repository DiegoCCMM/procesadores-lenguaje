//*****************************************************************
// File:   SymbolTable.java
// Author: Procesadores de Lenguajes-University of Zaragoza
// Date:   julio 2021
// Coms:   Atributos públicos para ahorrarnos el uso de getters y setters
//*****************************************************************

//la tabla de símbolos será un ArrayList de diccionarios (HashMap<String, Symbol>), manejada como
//una pila: se inserta y accede por la derecha
//Cada nuevo bloque se apilará, guardando los símbolos en el diccionario correspondiente
//El constructor ya genera el primer bloque, vacío inicialmente.

//https://quick-adviser.com/can-a-hashmap-have-multiple-values-for-same-key/
//HashMap doesn’t allow duplicate keys but allows duplicate values. That means A 
//single key can’t contain more than 1 value but more than 1 key can contain a single value.
//HashMap allows null key also but only once and multiple null values.
//https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html

package lib.symbolTable;

import java.util.*;
import lib.symbolTable.exceptions.SymbolNotFoundException;
import lib.symbolTable.exceptions.AlreadyDefinedSymbolException;

public class SymbolTable {
	private final int ST_SIZE = 16; //hasta 16 niveles
    private final int HASH_SIZE = 1024; //buckets
    private ArrayList<HashMap<String, Symbol>> st;
    private Set<String> reservedWords;

    public int level; //nivel actual
    public ArrayList <Integer> direcciones_de_los_bloques;

    public SymbolTable() {
        st = new ArrayList<HashMap<String, Symbol>>(ST_SIZE);
        level = -1; //aún no hay ningún bloque intoducido
        direcciones_de_los_bloques = new ArrayList<>();
        insertBlock();
        reservedWords = new HashSet<String> ();
    }

    public SymbolTable(String[] pals) {
    	st = new ArrayList<HashMap<String, Symbol>>(ST_SIZE);
        level = -1; //aún no hay ningún bloque introducido
        direcciones_de_los_bloques = new ArrayList<>();
        insertBlock();
        reservedWords = new HashSet<String>(Arrays.asList(pals));
    }
    
    //apila un nuevo bloque
    public void insertBlock() {
        st.add(new HashMap<String, Symbol>(HASH_SIZE));
        level++;
        direcciones_de_los_bloques.add(3);
    }

    //elimina un bloque
    public void removeBlock() {
        st.remove(st.size()-1);
        level--;
        direcciones_de_los_bloques.remove(direcciones_de_los_bloques.size() - 1);
    }

    //inserta las palabras reservadas, sustituyendo el anterior contenido
    public void insertReservedWords(String[] pals) {
        reservedWords = new HashSet<String>(Arrays.asList(pals));
    }
    
    public boolean isReservedWord (String word)  
    {
    	return reservedWords.contains(word); 
    }
    
    //Si un símbolo con el mismo nombre está o es palabra reservada, excepción. 
    //Si no, se inserta
    public void insertSymbol(Symbol s) throws AlreadyDefinedSymbolException {
        if(reservedWords.contains(s.name)) {
            throw new AlreadyDefinedSymbolException(s.name);
        }
        HashMap<String, Symbol> currentBlock = st.get(st.size()-1);
        if (currentBlock.containsKey(s.name)) { // ya está
            throw new AlreadyDefinedSymbolException(s.name);
        } else {
            int nivel_actual = level;
            int direccion_post_reserva_del_simbolo =
                    direcciones_de_los_bloques.get(nivel_actual) + el_tamanyo_del_simbolo(s);
            s.nivel = level;
            s.dir = direcciones_de_los_bloques.get(nivel_actual);
            direcciones_de_los_bloques.set(nivel_actual, direccion_post_reserva_del_simbolo);
            currentBlock.put(s.name, s);
        }
    }

    private Integer el_tamanyo_del_simbolo(Symbol s) {
        if(s instanceof SymbolArray)
        {
            return ((SymbolArray) s).maxInd + 1;
        }else{
            return 1;
        }
    }

    //Si no está, excepción. Si está, devuelve su referencia
    public Symbol getSymbol (String name) throws SymbolNotFoundException {
    	Symbol result = findSymbol(name); 
        if (result == null) {
        	throw new SymbolNotFoundException(name);
        }
        return result; 
    }
    
    // comprueba si está el símbolo 
    public boolean containsSymbol (String name) {
    	return findSymbol(name) != null; 
    }
    
    //para usar en "getSymbol" y "containsSymbol"
    private Symbol findSymbol (String name) {
    	for (int i=st.size()-1; i>=0; i--) {
            if (st.get(i).containsKey(name)) {
                return st.get(i).get(name);
            }
        }
        return null; 
    }

    //devuelve la tabla como un string
    public String toString() {
    	final String linea = "------------------------------------------------\n";
    	StringBuilder builder = new StringBuilder(); 
        builder.append(linea); 
        String tabs = "";
        for (int i=0; i<st.size(); i++) {
            for (Map.Entry entry : st.get(i).entrySet()) {
				//crear secuencia de tabuladores
                tabs = new String(new char[i]).replace("\0", "\t");
                builder.append(tabs); 
                builder.append(entry.toString()); 
                builder.append("\n"); 
            }
        }
        builder.append(linea); 
        return builder.toString();
    }
}