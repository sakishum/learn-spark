  
创建文件实际由以下代码完成
```
 static DFSOutputStream newStreamForCreate(DFSClient dfsClient, String src,
      FsPermission masked, EnumSet<CreateFlag> flag, boolean createParent,
      short replication, long blockSize, Progressable progress, int buffersize,
      DataChecksum checksum, String[] favoredNodes) throws IOException {
    HdfsFileStatus stat = null;

    // Retry the create if we get a RetryStartFileException up to a maximum
    // number of times
    boolean shouldRetry = true;
    int retryCount = CREATE_RETRY_COUNT;
    while (shouldRetry) {
      shouldRetry = false;
      try {
        stat = dfsClient.namenode.create(src, masked, dfsClient.clientName,
            new EnumSetWritable<CreateFlag>(flag), createParent, replication,
            blockSize, SUPPORTED_CRYPTO_VERSIONS);
        break;
      } catch (RemoteException re) {
        IOException e = re.unwrapRemoteException(
            AccessControlException.class,
            DSQuotaExceededException.class,
            FileAlreadyExistsException.class,
            FileNotFoundException.class,
            ParentNotDirectoryException.class,
            NSQuotaExceededException.class,
            RetryStartFileException.class,
            SafeModeException.class,
            UnresolvedPathException.class,
            SnapshotAccessControlException.class,
            UnknownCryptoProtocolVersionException.class);
        if (e instanceof RetryStartFileException) {
          if (retryCount > 0) {
            shouldRetry = true;
            retryCount--;
          } else {
            throw new IOException("Too many retries because of encryption" +
                " zone operations", e);
          }
        } else {
          throw e;
        }
      }
    }
    Preconditions.checkNotNull(stat, "HdfsFileStatus should not be null!");
    final DFSOutputStream out = new DFSOutputStream(dfsClient, src, stat,
        flag, progress, checksum, favoredNodes);
    out.start();
    return out;
  }
    ```


public class DistributedFileSystem extends FileSystem {
public class HdfsDataOutputStream extends FSDataOutputStream {




 class DataStreamer extends Daemon {
DataStreamer：负责发送数据


// Processes responses from the datanodes.  A packet is removed
// from the ackQueue when its response arrives.
 //  private class ResponseProcessor extends Daemon { 
ResponseProcessor：接收来自datanode的反馈，如果接收成功将packet从dataQueue删除    


