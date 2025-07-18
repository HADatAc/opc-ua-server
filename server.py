from opcua import Server
import time

def main():
    server = Server()
    server.set_endpoint("opc.tcp://0.0.0.0:4840/freeopcua/server/")
    namespace = server.register_namespace("urn:example:weatherstation")

    objects = server.get_objects_node()
    myobj = objects.add_object(namespace, "MyObjects")

    temperature = myobj.add_variable(namespace, "Temperature", 20.0)
    temperature.set_writable()

    server.start()
    print("Servidor OPC UA rodando na opc.tcp://0.0.0.0:4840/freeopcua/server/")
    try:
        while True:
            time.sleep(1)
            val = temperature.get_value()
            temperature.set_value(val + 0.1)  # Atualiza a temperatura
    finally:
        server.stop()
        print("Servidor parado")

if __name__ == "__main__":
    main()
