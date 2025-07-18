from opcua import Server
import time

def main():
    server = Server()
    server.set_endpoint("opc.tcp://0.0.0.0:4840/freeopcua/server/")
    namespace = server.register_namespace("urn:example:weatherstation")

    objects = server.get_objects_node()
    ws_object = objects.add_object(namespace, "wsaheadhout")

    variables = {
        "ESP32_Chip_ID": "",
        "ESP32_Chip_Arrowhead_Project_ID": "",
        "timestamp": "",
        "temperature_dht11_C": 0.0,
        "humidity_dht11_percent": 0.0,
        "air_quality_V": 0.0,
        "temperature_baro_C": 0.0,
        "pressure_baro_Pa": 0.0,
    }

    var_nodes = {}
    for var_name, initial_value in variables.items():
        var_node = ws_object.add_variable(namespace, var_name, initial_value)
        var_node.set_writable() 
        var_nodes[var_name] = var_node

    server.start()
    print("Servidor OPC UA rodando na opc.tcp://0.0.0.0:4840/freeopcua/server/")
    try:
        while True:
            time.sleep(30)
            # Aqui servidor pode ler os valores atuais (se quiser monitorar)
            for var_name, var_node in var_nodes.items():
                val = var_node.get_value()
                print(f"{var_name}: {val}")
    finally:
        server.stop()
        print("Servidor parado")

if __name__ == "__main__":
    main()
