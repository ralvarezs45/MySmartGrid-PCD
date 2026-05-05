package grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.54.0)",
    comments = "Source: filtro.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class FiltradoGrpc {

  private FiltradoGrpc() {}

  public static final String SERVICE_NAME = "Filtrado";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<grpc.FiltroOuterClass.FiltroRequest,
      grpc.FiltroOuterClass.FiltroReply> getFiltrarMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "filtrar",
      requestType = grpc.FiltroOuterClass.FiltroRequest.class,
      responseType = grpc.FiltroOuterClass.FiltroReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<grpc.FiltroOuterClass.FiltroRequest,
      grpc.FiltroOuterClass.FiltroReply> getFiltrarMethod() {
    io.grpc.MethodDescriptor<grpc.FiltroOuterClass.FiltroRequest, grpc.FiltroOuterClass.FiltroReply> getFiltrarMethod;
    if ((getFiltrarMethod = FiltradoGrpc.getFiltrarMethod) == null) {
      synchronized (FiltradoGrpc.class) {
        if ((getFiltrarMethod = FiltradoGrpc.getFiltrarMethod) == null) {
          FiltradoGrpc.getFiltrarMethod = getFiltrarMethod =
              io.grpc.MethodDescriptor.<grpc.FiltroOuterClass.FiltroRequest, grpc.FiltroOuterClass.FiltroReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "filtrar"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.FiltroOuterClass.FiltroRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.FiltroOuterClass.FiltroReply.getDefaultInstance()))
              .setSchemaDescriptor(new FiltradoMethodDescriptorSupplier("filtrar"))
              .build();
        }
      }
    }
    return getFiltrarMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static FiltradoStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FiltradoStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FiltradoStub>() {
        @java.lang.Override
        public FiltradoStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FiltradoStub(channel, callOptions);
        }
      };
    return FiltradoStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static FiltradoBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FiltradoBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FiltradoBlockingStub>() {
        @java.lang.Override
        public FiltradoBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FiltradoBlockingStub(channel, callOptions);
        }
      };
    return FiltradoBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static FiltradoFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FiltradoFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FiltradoFutureStub>() {
        @java.lang.Override
        public FiltradoFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FiltradoFutureStub(channel, callOptions);
        }
      };
    return FiltradoFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default io.grpc.stub.StreamObserver<grpc.FiltroOuterClass.FiltroRequest> filtrar(
        io.grpc.stub.StreamObserver<grpc.FiltroOuterClass.FiltroReply> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getFiltrarMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service Filtrado.
   */
  public static abstract class FiltradoImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return FiltradoGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service Filtrado.
   */
  public static final class FiltradoStub
      extends io.grpc.stub.AbstractAsyncStub<FiltradoStub> {
    private FiltradoStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FiltradoStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FiltradoStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<grpc.FiltroOuterClass.FiltroRequest> filtrar(
        io.grpc.stub.StreamObserver<grpc.FiltroOuterClass.FiltroReply> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(
          getChannel().newCall(getFiltrarMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service Filtrado.
   */
  public static final class FiltradoBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<FiltradoBlockingStub> {
    private FiltradoBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FiltradoBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FiltradoBlockingStub(channel, callOptions);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service Filtrado.
   */
  public static final class FiltradoFutureStub
      extends io.grpc.stub.AbstractFutureStub<FiltradoFutureStub> {
    private FiltradoFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected FiltradoFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FiltradoFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_FILTRAR = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_FILTRAR:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.filtrar(
              (io.grpc.stub.StreamObserver<grpc.FiltroOuterClass.FiltroReply>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getFiltrarMethod(),
          io.grpc.stub.ServerCalls.asyncBidiStreamingCall(
            new MethodHandlers<
              grpc.FiltroOuterClass.FiltroRequest,
              grpc.FiltroOuterClass.FiltroReply>(
                service, METHODID_FILTRAR)))
        .build();
  }

  private static abstract class FiltradoBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    FiltradoBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return grpc.FiltroOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("Filtrado");
    }
  }

  private static final class FiltradoFileDescriptorSupplier
      extends FiltradoBaseDescriptorSupplier {
    FiltradoFileDescriptorSupplier() {}
  }

  private static final class FiltradoMethodDescriptorSupplier
      extends FiltradoBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    FiltradoMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (FiltradoGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new FiltradoFileDescriptorSupplier())
              .addMethod(getFiltrarMethod())
              .build();
        }
      }
    }
    return result;
  }
}
