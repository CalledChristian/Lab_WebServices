package com.example.labwebservice.Controller;

import com.example.labwebservice.Entity.Character;
import com.example.labwebservice.Repository.CharacterRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController //Equivalente a @Controller + @ResponseBody (el "return" Envia informacion)
@RequestMapping("/ws/personaje")

public class WSController {

    final CharacterRepository characterRepository;

    public WSController(CharacterRepository characterRepository) {

        this.characterRepository = characterRepository;
    }

    //Listar Productos
    @GetMapping("/list")
    public List<Character> listarPersonajes() {

        return characterRepository.findAll();
    }

    //Lista Producto por ID
    @GetMapping("/get/{id}")
    public ResponseEntity<HashMap<String,Object>> obtenerPersonajePorId(
            @PathVariable("id") String idStr) {

        //Nuevo objeto: "responseJson" tipo HashMap
        HashMap<String,Object> responseJson = new HashMap<>();

        try {
            int id = Integer.parseInt(idStr);
            Optional<Character> optPersonaje = characterRepository.findById(id);
            if (optPersonaje.isPresent()) {
                //"para mandar un mensaje , objeto , lista , etc al postman"
                //responseJson.put("Nombre de Variable",Valor String,integer,big decimal,etc);
                //responseJson.put("result","success");
                responseJson.put("personaje",optPersonaje.get());
                //HTTP- 200 OK
                return ResponseEntity.ok(responseJson);
            } else {
                //PRODUCTO NO ENCONTRADO
                responseJson.put("error","ID Personaje NO encontrado");
                responseJson.put("date", DateTimeFormatter.ofPattern("YY-MM-DD hh:mm:ss").format(LocalDateTime.now()));
            }
        } catch (NumberFormatException e) {
            //para mandar un mensaje al postman. ID en formato incorrecto
            responseJson.put("error","ID Personaje NO encontrado");
            responseJson.put("date", DateTimeFormatter.ofPattern("YY-MM-DD hh:mm:ss").format(LocalDateTime.now()));
        }
        //responseJson.put("result","failure");
        //HTTP 404 BAD REQUEST - Solicitud Erronea
        //return ResponseEntity.badRequest().body(responseJson);
        return new ResponseEntity<>(responseJson, HttpStatus.NOT_FOUND);
    }

    //Crear Producto
    @PostMapping(value="/save")

    /*En la interfaz diamante < > se coloca el tipo de dato que se enviará como cuerpo del mensaje,
    en este caso,Un HashMap de <String,Object>,
    pues este tipo de dato, responde al formato JSON de {llave: valor} */

    public ResponseEntity<HashMap<String,Object>> guardarPersonaje(
            //Con request body , se obtiene el objeto
            @RequestBody Character character,
            //Parametro opcional para mostrar el ID del producto recien creado
            @RequestParam(value="fetchId",required = false) boolean fetchId){

        //Nuevo objeto: "responseMap" tipo HashMap
        HashMap<String,Object> responseMap = new HashMap<>();

        //guardamos el producto nuevo
        characterRepository.save(character);
        //si el usuario pone en la url "?fetchId=true , entonces
        //al momento de crear un personaje , aparece tambien su nueva id creada
        if (fetchId) {
            //"para mandar un mensaje , objeto , lista , etc al postman"
            //responseMap.put("Nombre de Variable",Valor String,integer,big decimal,etc);
            responseMap.put("id",character.getId());
        }
        //responseMap.put("estado","failure");
        //HTTP  201 CREATED
        responseMap.put("msg","personaje creado");
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
        //para que no salga ningun mensaje
        //return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //Manejo de Excepciones para POST Y PUT
    //En caso no se envíe el producto en formato JSON
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<HashMap<String,Object>> gestionExcepcion(
            HttpServletRequest request) {

        //Nuevo objeto: "responseMap" tipo HashMap
        HashMap<String,Object> responseMap = new HashMap<>();
        //Al igual que se realizó con el POST,
        // se debe guardar (POST) O actualizar (put)
        // Método para gestionar la excepción en caso no se envíe el producto en formato JSON
        if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
            //si el metodo es PO
            responseMap.put("error","Error en validacion de datos");
            responseMap.put("date", DateTimeFormatter.ofPattern("YY-MM-DD hh:mm:ss").format(LocalDateTime.now()));
        }
        //HTTP 404 BAD REQUEST
        return ResponseEntity.badRequest().body(responseMap);
    }

    //Actualizar Producto Cmpleto: con todos los campos llenos en JSON
    // (OJO:Si un campo no se envia en el json , se actualiza como NULL)
    //USO DE PUTMAPPING

    @PutMapping(value = "/save")
    public ResponseEntity<HashMap<String,Object>> actualizarProducto(
            //CON REQUEST BODY, SE RECIBE EL PRODUCTO
            @RequestBody Character character) {

        HashMap<String, Object> responseMap = new HashMap<>();

        if (character.getId() != null && character.getId() > 0) {
            Optional <Character> opt = characterRepository.findById(character.getId());
            if (opt.isPresent()) {
                //SI EXISTE EL PERSONAJE , ACTUALIZAMOS
                characterRepository.save(character);
                //ENVIAMOS MENSAJE DE EXITO
                responseMap.put("msg", "personaje actualizado");
                // HTTP 200 OK
                return ResponseEntity.ok(responseMap);
            } else {
                //ENVIAMOS MENSAJE DE ERROR
                responseMap.put("msg", "El producto a actualizar no existe");
                // HTTP 404 BAD REQUEST
                return ResponseEntity.badRequest().body(responseMap);
            }
        } else {
            responseMap.put("msg", "Debe enviar un ID");
            // HTTP 404 BAD REQUEST
            return ResponseEntity.badRequest().body(responseMap);
        }
    }

    //Actualizacion Parcial -- QUE SOLO SE EDITEN LOS CAMPOS QUE SE ENVIEN EN EL JSON

    @PutMapping(value = "/save/parcial")
    public ResponseEntity<HashMap<String, Object>> actualizarProductoParcial(@RequestBody Character character) {

        HashMap<String, Object> responseMap = new HashMap<>();

        if (character.getId() != null && character.getId() > 0) {
            //si su id no es nulo y mayor a cero , entonces existe el producto
            Optional<Character> opt = characterRepository.findById(character.getId());
            if (opt.isPresent()) {
                Character character1 = opt.get();
                //validamos campo por campo si es diferente de nulo , se actualiza el campo
                if (character.getName() != null && character.getUrl() != null) {
                    character1.setName(character.getName());
                    character1.setUrl(character.getUrl());

                    if (character.getIdentity() != null)
                        character1.setIdentity(character.getIdentity());

                    if (character.getAlign() != null)
                        character1.setAlign(character.getAlign());

                    /*if (product.getUnitsInStock() != null)
                        productFromDb.setUnitsInStock(product.getUnitsInStock());

                    if (product.getUnitsOnOrder() != null)
                        productFromDb.setUnitsOnOrder(product.getUnitsOnOrder());

                    if (product.getSupplierID() != null)
                        productFromDb.setSupplierID(product.getSupplierID());

                    if (product.getCategoryID() != null)
                        productFromDb.setCategoryID(product.getCategoryID());

                    if (product.getQuantityPerUnit() != null)
                        productFromDb.setQuantityPerUnit(product.getQuantityPerUnit());

                    if (product.getReorderLevel() != null)
                        productFromDb.setReorderLevel(product.getReorderLevel());

                    if (product.getDiscontinued() != null)
                        productFromDb.setDiscontinued(product.getDiscontinued()); */

                    characterRepository.save(character1);
                    responseMap.put("estado", "actualizado");
                    return ResponseEntity.ok(responseMap);
                }else{
                    responseMap.put("msg", "el nombre y url no pueden ser nulos");
                }
            } else {
                responseMap.put("msg", "El producto a actualizar no existe");
            }
        } else {
            responseMap.put("msg", "Debe enviar un ID");
        }
        responseMap.put("estado", "error");
        return ResponseEntity.badRequest().body(responseMap);
    }

    //CREAR Y ACTUALIZAR PERSONAJE EN UN SOLO POST
    @PostMapping(value = "/guardar")
    public ResponseEntity<HashMap<String, Object>> crearyActualizarPersonaje(@RequestBody Character character) {

        HashMap<String, Object> responseMap = new HashMap<>();

        if (character.getId() != null && character.getId() > 0) {
            //si su id no es nulo y mayor a cero , entonces existe el producto
            Optional<Character> opt = characterRepository.findById(character.getId());
            if (opt.isPresent()) {
                Character character1 = opt.get();
                //validamos campo por campo si es diferente de nulo , se actualiza el campo
                if (character.getName() != null && character.getUrl() != null) {
                    character1.setName(character.getName());
                    character1.setUrl(character.getUrl());

                    if (character.getIdentity() != null)
                        character1.setIdentity(character.getIdentity());

                    if (character.getAlign() != null)
                        character1.setAlign(character.getAlign());

                    /*if (product.getUnitsInStock() != null)
                        productFromDb.setUnitsInStock(product.getUnitsInStock());

                    if (product.getUnitsOnOrder() != null)
                        productFromDb.setUnitsOnOrder(product.getUnitsOnOrder());

                    if (product.getSupplierID() != null)
                        productFromDb.setSupplierID(product.getSupplierID());

                    if (product.getCategoryID() != null)
                        productFromDb.setCategoryID(product.getCategoryID());

                    if (product.getQuantityPerUnit() != null)
                        productFromDb.setQuantityPerUnit(product.getQuantityPerUnit());

                    if (product.getReorderLevel() != null)
                        productFromDb.setReorderLevel(product.getReorderLevel());

                    if (product.getDiscontinued() != null)
                        productFromDb.setDiscontinued(product.getDiscontinued()); */

                    characterRepository.save(character1);
                    responseMap.put("estado", "actualizado");
                    return ResponseEntity.ok(responseMap);
                }else{
                    responseMap.put("msg", "el nombre y url no pueden ser nulos");
                }
            } else {
                responseMap.put("msg", "El producto a actualizar no existe");
                responseMap.put("date", DateTimeFormatter.ofPattern("YY-MM-DD hh:mm:ss").format(LocalDateTime.now()));
                return new ResponseEntity<>(responseMap, HttpStatus.NOT_FOUND);
            }
        } else {
            //entonces como el id es nulo , se esta creando un producto nuevo ,entonces
            //lo guardamos con SAVE
            characterRepository.save(character);
            responseMap.put("msg", "Personaje Creado");
            return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
        }
        responseMap.put("error","Error en validacion de datos");
        responseMap.put("date", DateTimeFormatter.ofPattern("YY-MM-DD hh:mm:ss").format(LocalDateTime.now()));
        return ResponseEntity.badRequest().body(responseMap);
    }


     //Borrar un productor por ID.

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<HashMap<String, Object>> borrarPersonaje(@PathVariable("id") String idStr) {

        HashMap<String, Object> responseMap = new HashMap<>();

        try {
            int id = Integer.parseInt(idStr);
            if (characterRepository.existsById(id)) {
                characterRepository.deleteById(id);
                responseMap.put("msg", "borrado exitoso");
                //con mensaje
                return ResponseEntity.ok(responseMap);
                //sin mensaje
                //return ResponseEntity.ok().build();
            } else {
                //responseMap.put("msg", "no se encontró el producto con id: " + id);
                responseMap.put("error", "ID Personaje NO encontrado");
                return new ResponseEntity<>(responseMap, HttpStatus.NOT_FOUND);
            }
        } catch (NumberFormatException ex) {
            responseMap.put("error", "ID Personaje NO encontrado");
            return new ResponseEntity<>(responseMap, HttpStatus.NOT_FOUND);
        }
    }

}

