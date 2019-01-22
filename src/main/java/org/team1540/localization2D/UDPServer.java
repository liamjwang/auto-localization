package org.team1540.localization2D;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class UDPServer implements Runnable {

  public static final int DOUBLE_LENGTH = 8;

  private Thread t;
  private DatagramSocket clientSocket;

  private long lastRecievedTime = 0;

  private double cmdVelX = 0;
  private double cmdVelTheta = 0;
  private double timeStamp = 0;

  private InetAddress IPAddress = InetAddress.getByName("10.15.40.75");

  UDPServer() throws SocketException, UnknownHostException {
    t = new Thread(this);
    System.out.println("UDP Server Thread Starting");
    t.start();
    clientSocket = new DatagramSocket();
  }

  @Override
  public void run() {
    try {
      DatagramSocket serverSocket = new DatagramSocket(5801);
      byte[] data = new byte[DOUBLE_LENGTH * 2];
      while (true) {
        DatagramPacket receivePacket = new DatagramPacket(data, data.length);
        serverSocket.receive(receivePacket);
        data = receivePacket.getData();
        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
//        timeStamp = buf.getDouble();
        cmdVelX = buf.getDouble();
//        System.out.println(cmdVelX);
        cmdVelTheta = buf.getDouble();
//        System.out.println("X: " + cmdVelX + " Theta: " + cmdVelTheta);
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
    DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 5800);
    clientSocket.send(sendPacket);
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