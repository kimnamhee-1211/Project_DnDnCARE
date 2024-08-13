package com.kh.dndncare.common;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;

@Component
public class ThumbnailUtil {
	
	public BufferedImage createThumbnail(BufferedImage image, String text, int x, int y) {
		// Creates a Graphics2D, which can be used to draw intothis BufferedImage.
		Graphics2D g2d = image.createGraphics();
		
		g2d.setFont(new Font("맑은 고딕", Font.PLAIN, 30)); // 삽입할 텍스트의 폰트와 크기를 지정
		g2d.setColor(Color.BLACK); // 삽입할 텍스트의 색상 지정
		g2d.drawString(text, x, y); // x == 25, y == 150
		g2d.dispose(); // Graphics2D 객체 소멸
		
		return image;
	}
	
	public void saveImage(BufferedImage image, String filePath) {
		try {
			ImageIO.write(image, "png", new File(filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
