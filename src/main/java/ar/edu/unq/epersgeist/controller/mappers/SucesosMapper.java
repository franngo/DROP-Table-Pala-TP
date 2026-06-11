package ar.edu.unq.epersgeist.controller.mappers;

import ar.edu.unq.epersgeist.controller.dtos.ubicaciones.response.SucesosResponseDTO;

import java.util.Map;

public class SucesosMapper {

    public static SucesosResponseDTO toDto(Map<String, Integer> map) {
        return new SucesosResponseDTO(
                map.get("exorcismos"),
                map.get("invocaciones demoniacas"),
                map.get("sucesos desconocidos")
        );
    }

//    assertEquals(2, map.get("exorcismos"));
//    assertEquals(2, map.get("invocaciones demoniacas"));
//    assertEquals(5, map.get("sucesos desconocidos"));

}
