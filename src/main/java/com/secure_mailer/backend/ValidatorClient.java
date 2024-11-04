package com.secure_mailer.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.sql.SQLException;
import java.io.*;

import org.apache.commons.codec.binary.Base32;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;

public class ValidatorClient {
	
	private Socket skt;
	
	private String fromEmailId;
	private String toEmailId;
	private String emlHash;
	private String timeStamp;
	private String authCode;
	
	
	public ValidatorClient() {
		try {
			skt = new Socket(InetAddress.getByName(null), 2000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Platform.runLater(() -> showAlert("Error", "Failed to connet with server."));
			e.printStackTrace();
		}
		
	}
	
	
	public ValidatorClient(String host, int port) {
		try {
			skt = new Socket(host, port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Platform.runLater(() -> showAlert("Error", "Failed to connet with server."));
			e.printStackTrace();
		}	
	}
	
	public void closeValidatorClient() {
		try {
			skt.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void sendRecord(String fromEmailId, String toEmailId,
			String emlHash ) throws SQLException {
		
		this.fromEmailId = fromEmailId;
		this.toEmailId = toEmailId;
		this.emlHash = emlHash;
		
		PrintStream ps = null;
		
		try {
			ps=new PrintStream(skt.getOutputStream());
			
			
			String secretKey = SecretCodeDAO.getSecretCode(toEmailId);
			long timeStep = Instant.now().getEpochSecond() / 30;
			this.timeStamp = Long.toString(timeStep);
			this.authCode = generateTOTP(secretKey, timeStep);
			
			String[] data = new String[] {
					this.fromEmailId,
					this.toEmailId,
					this.timeStamp,
					this.authCode,
					this.emlHash
			};
			
			System.out.println(this.fromEmailId);
			System.out.println(this.toEmailId);
			System.out.println(this.timeStamp);
			System.out.println(this.authCode);
			System.out.println(this.emlHash);
			
			String packet = formPacket(data);
				
			//Packet ready.
			packet = String.valueOf(0) + packet;
			
			//Send the packet.
			ps.println(packet);
			
			
			// If successful, show the alert on the JavaFX thread
            Platform.runLater(() -> showAlert("Message Sent", "Your message has been sent successfully!"));
			
		} catch(SQLException e) {
			
			Platform.runLater(() -> showAlert("Error", "Failed to retrieve Secret key."));
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			Platform.runLater(() -> showAlert("Error", "Failed to send the message."));
			e.printStackTrace();
		}
		
		
		
	}
	
	public boolean bringCode(String fromEmailId, String toEmailId, String emlHash) {
		
		this.fromEmailId = fromEmailId;
		this.toEmailId = toEmailId;
		this.emlHash = emlHash;
		
		PrintStream ps = null;
		BufferedReader br= null;

		try {
			ps=new PrintStream(skt.getOutputStream());
			br=new BufferedReader(new InputStreamReader(skt.getInputStream()));
			
			String[] data = new String[] {
					this.emlHash
			};
			
			
			String packet = formPacket(data);
			
			//Packet ready.
			packet = String.valueOf(1) + packet;
			
			//Send the size of packet.
			//ps.println(packet.length());
			
			//Send the packet.
			ps.println(packet);
			
			//Receive packet.
			
			String msg = br.readLine();
			
			//Parse the packet.
			int i = 0;
			String num = "";
			for(; i < msg.length() ; i++ ) {
				if( msg.charAt(i) != ':' )
					num += Character.toString(msg.charAt(i));
				else
					break;
			}
			
			
			if( num.equals("420") ) {
				Platform.runLater(() -> showAlert("Error", "Email record not found on server."));
				return false;
			}
			
			this.timeStamp = num;
			i = i + 1;
			num = "";
			for(; i < msg.length() ; i++ ) {	
				num += Character.toString(msg.charAt(i));	
			}
			this.authCode = num;
			
			String secretKey = SecretCodeDAO.getSecretCode(fromEmailId);
			String totp = generateTOTP( secretKey, Long.parseLong(this.timeStamp));
			
			
		
			return (totp == this.authCode);
			
		} catch(SQLException e) {
			
			Platform.runLater(() -> showAlert("Error", "Failed to retrieve Secret key."));
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Platform.runLater(() -> showAlert("Error", "Failed to send communicate with server for authentication."));
			e.printStackTrace();
			return false;
		}
		
	}
	
	private String formPacket(String[] data) {
		
		int dataSize = data.length;
		
		String packet = "";
		int stringSize;
		for(int i = 0 ; i < dataSize ; i++ ) {
			stringSize = data[i].length();
			packet += String.valueOf(stringSize) + ":" + data[i];
		}
		
		return packet;
	}
	
	
	public String generateTOTP(String secretKey, long timeStep) {
        try {
            Base32 base32 = new Base32();
            byte[] bytes = base32.decode(secretKey);
            SecretKeySpec secretKeySpec = new SecretKeySpec(bytes, "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKeySpec);

            // Current Unix time / 30 (TOTP time step)
            //long timeStep = Instant.now().getEpochSecond() / 30;
            byte[] data = new byte[8];
            for (int i = 7; timeStep > 0; i--) {
                data[i] = (byte) (timeStep & 0xFF);
                timeStep >>= 8;
            }

            byte[] hash = mac.doFinal(data);
            int offset = hash[hash.length - 1] & 0xF;
            int otp = ((hash[offset] & 0x7F) << 24 | (hash[offset + 1] & 0xFF) << 16 | 
                       (hash[offset + 2] & 0xFF) << 8 | (hash[offset + 3] & 0xFF)) % 1_000_000;

            return String.format("%06d", otp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	private void showAlert(String title, String content) {
	    Alert alert = new Alert(AlertType.INFORMATION);
	    alert.setTitle(title);
	    alert.setHeaderText(null);
	    alert.setContentText(content);
	    alert.show();

	    // Create a PauseTransition for 10 seconds
	    PauseTransition delay = new PauseTransition(Duration.seconds(10));
	    delay.setOnFinished(event -> alert.close()); // Close alert after 10 seconds
	    delay.play(); // Start the timer
	}

}
	
	
	
	
