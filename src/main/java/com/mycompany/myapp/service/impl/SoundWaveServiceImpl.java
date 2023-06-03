package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.SoundWav;
import com.mycompany.myapp.repository.SoundWavRepository;
import com.mycompany.myapp.service.SoundWavService;

import java.awt.*;
import java.io.*;
import java.util.Date;
import java.util.Optional;

import com.mycompany.myapp.service.dto.SoundWavDTO;
import com.mycompany.myapp.service.mapper.SoundWavMapper;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

@Service
@Transactional
public class SoundWaveServiceImpl implements SoundWavService {
    private  final SoundWavRepository soundWavRepository;
    private  final SoundWavMapper soundWavMapper;

    public SoundWaveServiceImpl(SoundWavRepository soundWavRepository, SoundWavMapper soundWavMapper) {
        this.soundWavRepository = soundWavRepository;
        this.soundWavMapper = soundWavMapper;
    }



    @Override
    public Boolean saveWaveData() {

        for (int j = 1; j <= 6; j++) {
            SoundWav soundWav = new SoundWav();
            String wavFilePath = "/wavs/"+j+".wav";

           SoundWavDTO soundWavs1= waveForm(wavFilePath);
           SoundWavDTO soundWavDTO = spectogram(wavFilePath);

           soundWav.setSoundclip(j+".wav");
           soundWav.setWaveForm(soundWavs1.getWaveForm());
           soundWav.setWaveFormContentType(soundWavs1.getWaveFormContentType());
           soundWav.setSpectogram(soundWavDTO.getSpectogram());
           soundWav.setSpectogramContentType(soundWavDTO.getSpectogramContentType());
           soundWavRepository.save(soundWav);

        }

        return null;
    }

    @Override
    public Optional<SoundWavDTO> findOne(String id) {
        return  soundWavRepository.findById(id)
            .map(soundWavMapper::toDto);
    }

    public SoundWavDTO waveForm(String path) {
        SoundWavDTO soundWavDTO = new SoundWavDTO();
        File fileReport = null;

        try {
            InputStream input = getClass().getResourceAsStream(path);
            fileReport = File.createTempFile(new Date().getTime() + "temp", ".wav");
            OutputStream out = new FileOutputStream(fileReport);
            int read;
            byte[] bytes = new byte[1024];

            while ((read = input.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
            input.close();
            fileReport.deleteOnExit();
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileReport);

            // Get audio format information
            int numChannels = audioInputStream.getFormat().getChannels();
            int sampleRate = (int) audioInputStream.getFormat().getSampleRate();

            // Read audio data into a buffer
            byte[] buffer = new byte[1024];
            int bytesRead;
            XYSeries series = new XYSeries("Waveform");

            while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i += numChannels * 2) {
                    // Convert bytes to short (assuming 16-bit audio)
                    short sample = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xff));
                    // Add the sample to the series
                    series.add(series.getItemCount(), sample);
                }
            }

            // Create dataset and chart
            XYSeriesCollection dataset = new XYSeriesCollection(series);
            JFreeChart chart = ChartFactory.createXYLineChart(
                "Waveform", "Time", "Amplitude", dataset, PlotOrientation.VERTICAL, false, false, false);

            // Customize chart appearance
            chart.setBackgroundPaint(Color.WHITE);
//            chart.setBackgroundPaint(Color.BLUE);

            chart.getXYPlot().getRangeAxis().setAutoRange(true);

            // Save the chart as an image file
            int width = 800;
            int height = 400;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(outputStream, chart, width, height);

            byte[] waveformData = outputStream.toByteArray();

            System.out.println("Waveform image saved successfully!");


            // Store the MP3 byte array and content type
            soundWavDTO.setWaveForm(waveformData);
            soundWavDTO.setWaveFormContentType("image/png");

            System.out.println("WAV to MP3 conversion successful!");
        } catch (IllegalArgumentException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        return soundWavDTO;
    }



    private static byte[] convertFileToByteArray(String filePath) throws IOException {
        File file = new File(filePath);
        byte[] byteArray = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(byteArray);
        }

        return byteArray;
    }
    public SoundWavDTO spectogram(String path){
        SoundWavDTO soundWavDTO = new SoundWavDTO();  File fileReport = null;

        try {
            InputStream input = getClass().getResourceAsStream(path);
            fileReport = File.createTempFile(new Date().getTime()+"temp", ".wav");
            OutputStream out = new FileOutputStream(fileReport);
            int read;
            byte[] bytes = new byte[1024];

            while ((read = input.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
            input.close();
            fileReport.deleteOnExit();
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(fileReport);
            // Get audio format information
            AudioFormat format = audioInputStream.getFormat();
            int numChannels = format.getChannels();
            float sampleRate = format.getSampleRate();

            // Read audio data into a buffer
            byte[] audioBuffer = new byte[(int) audioInputStream.getFrameLength() * format.getFrameSize()];
            audioInputStream.read(audioBuffer);

            // Create dataset for spectrogram
            DefaultXYZDataset dataset = createSpectrogramDataset(audioBuffer, format, numChannels, sampleRate);

            // Create spectrogram chart
            JFreeChart chart = ChartFactory.createBubbleChart(
                "Spectrogram", "Time", "Frequency", dataset, PlotOrientation.VERTICAL, false, false, false);

            // Customize chart appearance
            chart.setBackgroundPaint(Color.WHITE);
            chart.getXYPlot().getRenderer().setSeriesPaint(0, Color.BLUE); // Customize bubble color

            // Save the chart as an image file

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ChartUtils.writeChartAsPNG(outputStream, chart, 800, 400);

            byte[] waveformData = outputStream.toByteArray();

            System.out.println("Waveform image saved successfully!");


            // Store the MP3 byte array and content type
            soundWavDTO.setSpectogram(waveformData);
            soundWavDTO.setSpectogramContentType("image/png");
            System.out.println("Spectrogram image saved successfully!");

        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        return soundWavDTO;
    }

    private static DefaultXYZDataset createSpectrogramDataset(byte[] audioData, AudioFormat format, int numChannels, float sampleRate) {
        int windowSize = 1024;  // Adjust the window size as needed
        int overlap = windowSize / 2;
        int fftSize = windowSize / 2;

        int frameLength = audioData.length / numChannels;
        int spectrogramWidth = (frameLength - windowSize) / (windowSize - overlap) + 1;
        int spectrogramHeight = fftSize / 2 + 1;

        DefaultXYZDataset dataset = new DefaultXYZDataset();

        double[][] data = new double[3][spectrogramWidth * spectrogramHeight];

        for (int channel = 0; channel < numChannels; channel++) {
            int dataIndex = 0;

            for (int i = 0; i < spectrogramWidth; i++) {
                double[] samples = new double[windowSize];

                for (int j = 0; j < windowSize; j++) {
                    int index = (i * (windowSize - overlap) + j) * numChannels + channel;
                    samples[j] = audioData[index];
                }

                double[] magnitudes = computeFFT(samples);

                for (int j = 0; j < spectrogramHeight; j++) {
                    double time = i * (windowSize - overlap) / sampleRate;
                    double frequency = j * sampleRate / fftSize;
                    double magnitude = magnitudes[j];

                    data[0][dataIndex] = time;
                    data[1][dataIndex] = frequency;
                    data[2][dataIndex] = magnitude;
                    dataIndex++;
                }
            }
        }

        dataset.addSeries("Spectrogram", data);


        return dataset;
    }

    private static double[] computeFFT(double[] samples) {
        // Perform Fast Fourier Transform (FFT) on the samples
        // Implement your FFT algorithm here
        // This is just a placeholder code that returns random magnitudes

        int fftSize = samples.length;

        double[] magnitudes = new double[fftSize / 2 + 1];
        for (int i = 0; i < magnitudes.length; i++) {
            magnitudes[i] = Math.random();  // Replace with your actual FFT calculation
        }

        return magnitudes;
    }
}
