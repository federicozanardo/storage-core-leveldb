package storage.module.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class StorageSerializer<T> {

    /**
     * This method allows to serialize data.
     *
     * @param data: data to serialize.
     * @return the data serialized, but if an error occurred, return null.
     */
    public byte[] serialize(T data) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream;

        try {
            outputStream = new ObjectOutputStream(byteArrayOutputStream);
            outputStream.writeObject(data);
            outputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException exception) {
            System.out.println("serialize: Error while serializing data");
        } finally {
            try {
                byteArrayOutputStream.close();
            } catch (IOException ex) {
                System.out.println("serialize: Error while closing the output stream");
            }
        }
        return null;
    }

    /**
     * This method allows to deserialize data.
     *
     * @param bytes: stream to deserialize.
     * @return the data deserialized, but if an error occurred, return null.
     */
    public T deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInput objectInput = null;

        try {
            objectInput = new ObjectInputStream(byteArrayInputStream);
            return (T) objectInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("deserialize: Error while deserializing data");
        } finally {
            try {
                if (objectInput != null) {
                    objectInput.close();
                }
                byteArrayInputStream.close();
            } catch (IOException ex) {
                System.out.println("deserialize: Error while closing the input stream");
            }
        }
        return null;
    }
}
