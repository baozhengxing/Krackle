package com.blackberry.kafka.lowoverhead.producer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.Deflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerConfiguration {
  private static final Logger LOG = LoggerFactory
      .getLogger(ProducerConfiguration.class);

  protected static final int ONE_MB = 1024 * 1024;

  // Options matching the producer client
  private List<String> metadataBrokerList;
  private short requestRequiredAcks;
  private int requestTimeoutMs;
  private String compressionCodec;
  private int messageSendMaxRetries;
  private int retryBackoffMs;
  private long topicMetadataRefreshIntervalMs;
  private long queueBufferingMaxMs;
  private long queueEnqueueTimeoutMs;
  private int sendBufferBytes;

  // Client specific options
  private int messageBufferSize;
  private int sendBufferSize;
  private int responseBufferSize;
  private int compressionLevel;

  public ProducerConfiguration(Properties props) throws Exception {
    LOG.info("Building configuration.");

    metadataBrokerList = new ArrayList<String>();
    String metadataBrokerListString = props.getProperty("metadata.broker.list");
    if (metadataBrokerListString == null || metadataBrokerListString.isEmpty()) {
      throw new Exception("metadata.broker.list cannot be empty.");
    }
    for (String s : metadataBrokerListString.split(",")) {
      // This is not a good regex. Could make it better.
      if (s.matches("^[\\.a-zA-Z0-9-]*:\\d+$")) {
        metadataBrokerList.add(s);
      } else {
        throw new Exception(
            "metata.broker.list must contain a list of hosts and ports (localhost:123,192.168.1.1:456).  Got "
                + metadataBrokerListString);
      }
    }
    LOG.info("metadata.broker.list = {}", metadataBrokerList);

    queueBufferingMaxMs = Long.parseLong(props.getProperty(
        "queue.buffering.max.ms", "5000"));
    if (queueBufferingMaxMs < 0) {
      throw new Exception("queue.buffering.max.ms cannot be negative.");
    }
    LOG.info("queue.buffering.max.ms = {}", queueBufferingMaxMs);

    requestRequiredAcks = Short.parseShort(props.getProperty(
        "request.required.acks", "1"));
    if (requestRequiredAcks != -1 && requestRequiredAcks != 0
        && requestRequiredAcks != 1) {
      throw new Exception("request.required.acks can only be -1, 0 or 1.  Got "
          + requestRequiredAcks);
    }
    LOG.info("request.required.acks = {}", requestRequiredAcks);

    requestTimeoutMs = Integer.parseInt(props.getProperty("request.timeout.ms",
        "10000"));
    if (requestTimeoutMs < 0) {
      throw new Exception("request.timeout.ms cannot be negative.  Got "
          + requestTimeoutMs);
    }
    LOG.info("request.timeout.ms = {}", requestTimeoutMs);

    messageSendMaxRetries = Integer.parseInt(props.getProperty(
        "message.send.max.retries", "3"));
    if (messageSendMaxRetries < 0) {
      throw new Exception("message.send.max.retries cannot be negative.  Got "
          + messageSendMaxRetries);
    }
    LOG.info("message.send.max.retries = {}", messageSendMaxRetries);

    retryBackoffMs = Integer.parseInt(props.getProperty("retry.backoff.ms",
        "100"));
    if (retryBackoffMs < 0) {
      throw new Exception("retry.backoff.ms cannot be negative.  Got "
          + retryBackoffMs);
    }
    LOG.info("retry.backoff.ms = " + retryBackoffMs);

    topicMetadataRefreshIntervalMs = Long.parseLong(props.getProperty(
        "topic.metadata.refresh.interval.ms", "" + (600 * 1000)));
    LOG.info("topic.metadata.refresh.interval.ms = {}",
        topicMetadataRefreshIntervalMs);

    messageBufferSize = Integer.parseInt(props.getProperty(
        "message.buffer.size", "" + ONE_MB));
    if (messageBufferSize < 1) {
      throw new Exception("message.buffer.size must be greater than 0.  Got "
          + messageBufferSize);
    }
    LOG.info("message.buffer.size = {}", messageBufferSize);

    sendBufferSize = Integer.parseInt(props.getProperty("send.buffer.size", ""
        + (messageBufferSize + 200)));
    if (sendBufferSize < 1) {
      throw new Exception(
          "message.send.max.retries must be greater than 0.  Got "
              + sendBufferSize);
    }
    LOG.info("send.buffer.size = {}", sendBufferSize);

    responseBufferSize = Integer.parseInt(props.getProperty(
        "response.buffer.size", "100"));
    if (responseBufferSize < 1) {
      throw new Exception("response.buffer.size must be greater than 0.  Got "
          + responseBufferSize);
    }
    LOG.info("response.buffer.size = {}", responseBufferSize);

    String rawCompressionCodec = props.getProperty("compression.codec", "none");
    compressionCodec = rawCompressionCodec.toLowerCase();
    if (compressionCodec.equals("none") == false
        && compressionCodec.equals("gzip") == false
        && compressionCodec.equals("snappy") == false) {
      throw new Exception(
          "compression.codec must be one of none, gzip or snappy.  Got "
              + rawCompressionCodec);
    }
    LOG.info("compression.codec = {}", compressionCodec);

    compressionLevel = Integer.parseInt(props.getProperty(
        "gzip.compression.level", "" + Deflater.DEFAULT_COMPRESSION));
    if (compressionLevel < -1 || compressionLevel > 9) {
      throw new Exception(
          "gzip.compression.level must be -1 (default), 0 (no compression) or in the range 1-9.  Got "
              + compressionLevel);
    }
    LOG.info("gzip.compression.level = {}", compressionLevel);

    queueEnqueueTimeoutMs = Long.parseLong(props.getProperty(
        "queue.enqueue.timeout.ms", "-1"));
    if (queueEnqueueTimeoutMs != -1 && queueEnqueueTimeoutMs < 0) {
      throw new Exception(
          "queue.enqueue.timeout.ms must either be -1 or a non-negative.");
    }
    LOG.info("queue.enqueue.timeout.ms = {}", queueEnqueueTimeoutMs);

    sendBufferBytes = Integer.parseInt(props.getProperty("send.buffer.bytes",
        "" + (100 * 1024)));
    LOG.info("send.buffer.bytes = {}", sendBufferBytes);
  }

  public List<String> getMetadataBrokerList() {
    return metadataBrokerList;
  }

  public void setMetadataBrokerList(List<String> metadataBrokerList) {
    this.metadataBrokerList = metadataBrokerList;
  }

  public long getQueueBufferingMaxMs() {
    return queueBufferingMaxMs;
  }

  public void setQueueBufferingMaxMs(long queueBufferingMaxMs) {
    this.queueBufferingMaxMs = queueBufferingMaxMs;
  }

  public short getRequestRequiredAcks() {
    return requestRequiredAcks;
  }

  public void setRequestRequiredAcks(short requestRequiredAcks) {
    this.requestRequiredAcks = requestRequiredAcks;
  }

  public int getRequestTimeoutMs() {
    return requestTimeoutMs;
  }

  public void setRequestTimeoutMs(int requestTimeoutMs) {
    this.requestTimeoutMs = requestTimeoutMs;
  }

  public int getMessageSendMaxRetries() {
    return messageSendMaxRetries;
  }

  public void setMessageSendMaxRetries(int messageSendMaxRetries) {
    this.messageSendMaxRetries = messageSendMaxRetries;
  }

  public int getRetryBackoffMs() {
    return retryBackoffMs;
  }

  public void setRetryBackoffMs(int retryBackoffMs) {
    this.retryBackoffMs = retryBackoffMs;
  }

  public int getMessageBufferSize() {
    return messageBufferSize;
  }

  public void setMessageBufferSize(int messageBufferSize) {
    this.messageBufferSize = messageBufferSize;
  }

  public int getSendBufferSize() {
    return sendBufferSize;
  }

  public void setSendBufferSize(int sendBufferSize) {
    this.sendBufferSize = sendBufferSize;
  }

  public int getResponseBufferSize() {
    return responseBufferSize;
  }

  public void setResponseBufferSize(int responseBufferSize) {
    this.responseBufferSize = responseBufferSize;
  }

  public String getCompressionCodec() {
    return compressionCodec;
  }

  public void setCompressionCodec(String compressionCodec) {
    this.compressionCodec = compressionCodec;
  }

  public int getCompressionLevel() {
    return compressionLevel;
  }

  public void setCompressionLevel(int compressionLevel) {
    this.compressionLevel = compressionLevel;
  }

  public long getTopicMetadataRefreshIntervalMs() {
    return topicMetadataRefreshIntervalMs;
  }

  public void setTopicMetadataRefreshIntervalMs(
      long topicMetadataRefreshIntervalMs) {
    this.topicMetadataRefreshIntervalMs = topicMetadataRefreshIntervalMs;
  }

  public long getQueueEnqueueTimeoutMs() {
    return queueEnqueueTimeoutMs;
  }

  public void setQueueEnqueueTimeoutMs(long queueEnqueueTimeoutMs) {
    this.queueEnqueueTimeoutMs = queueEnqueueTimeoutMs;
  }

}