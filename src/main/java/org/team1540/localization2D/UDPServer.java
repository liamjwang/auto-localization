package org.team1540.localization2D;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class UDPServer implements Runnable {

  public static final int DOUBLE_LENGTH = 8;

  private Thread t;
  private DatagramSocket clientSocket;

  private long lastRecievedTime = 0;

  private double cmdVelX = 0;
  private double cmdVelTheta = 0;

  private InetAddress IPAddress = InetAddress.getByName("DESKTOP-B28AO.local");

  UDPServer() throws SocketException, UnknownHostException {
    t = new Thread(this);
    System.out.println("UDP Server Thread Starting");
    t.start();
    clientSocket = new DatagramSocket();
  }

  @Override
  public void run() {
    try {
      DatagramSocket serverSocket = new DatagramSocket(9876);
      byte[] data = new byte[DOUBLE_LENGTH * 2];
      while (true) {
        DatagramPacket receivePacket = new DatagramPacket(data, data.length);
        System.out.println("Waiting for packet");
        serverSocket.receive(receivePacket);
        System.out.println(receivePacket.getPort());
        data = receivePacket.getData();
        ByteBuffer buf = ByteBuffer.wrap(data);
        cmdVelX = buf.getDouble();
        cmdVelTheta = buf.getDouble();
        lastRecievedTime = System.currentTimeMillis();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void sendPoseAndTwist(double poseX, double poseY, double poseTheta, double twistX, double twistOmega) throws IOException {
    byte[] data = ByteBuffer.allocate(DOUBLE_LENGTH * 5)
        .putDouble(poseX)
        .putDouble(poseY)
        .putDouble(poseTheta)
        .putDouble(twistX)
        .putDouble(twistOmega)
        .array();
    DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 4445);
    clientSocket.send(sendPacket);

    System.out.println("Data sent: "+IPAddress.toString());
  }

  public static double toDouble(byte[] bytes) {
    return ByteBuffer.wrap(bytes).getDouble();
  }

  public static byte[] toByteArray(double value) {
    return ByteBuffer.wrap(new byte[8]).putDouble(value).array();
  }


  public double getCmdVelX() {
    if (System.currentTimeMillis()-lastRecievedTime < Tuning.drivetrainUDPTimeout) {
      return cmdVelX;
    } else {
      return 0;
    }
  }

  public double getCmdVelTheta() {
    if (System.currentTimeMillis()-lastRecievedTime < Tuning.drivetrainUDPTimeout) {
      return cmdVelTheta;
    } else {
      return 0;
    }
  }
}