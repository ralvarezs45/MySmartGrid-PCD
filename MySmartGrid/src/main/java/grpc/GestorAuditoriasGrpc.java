package grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.54.0)",
    comments = "Source: auditoria.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class GestorAuditoriasGrpc {

  private GestorAuditoriasGrpc() {}

  public static final String SERVICE_NAME = "GestorAuditorias";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<grpc.AuditoriaOuterClass.AuditoriaRequest,
      grpc.AuditoriaOuterClass.AuditoriaReply> getObtenerConsumosPeligrososMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "obtenerConsumosPeligrosos",
      requestType = grpc.AuditoriaOuterClass.AuditoriaRequest.class,
      responseType = grpc.AuditoriaOuterClass.AuditoriaReply.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<grpc.AuditoriaOuterClass.AuditoriaRequest,
      grpc.AuditoriaOuterClass.AuditoriaReply> getObtenerConsumosPeligrososMethod() {
    io.grpc.MethodDescriptor<grpc.AuditoriaOuterClass.AuditoriaRequest, grpc.AuditoriaOuterClass.AuditoriaReply> getObtenerConsumosPeligrososMethod;
    if ((getObtenerConsumosPeligrososMethod = GestorAuditoriasGrpc.getObtenerConsumosPeligrososMethod) == null) {
      synchronized (GestorAuditoriasGrpc.class) {
        if ((getObtenerConsumosPeligrososMethod = GestorAuditoriasGrpc.getObtenerConsumosPeligrososMethod) == null) {
          GestorAuditoriasGrpc.getObtenerConsumosPeligrososMethod = getObtenerConsumosPeligrososMethod =
              io.grpc.MethodDescriptor.<grpc.AuditoriaOuterClass.AuditoriaRequest, grpc.AuditoriaOuterClass.AuditoriaReply>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "obtenerConsumosPeligrosos"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.AuditoriaOuterClass.AuditoriaRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  grpc.AuditoriaOuterClass.AuditoriaReply.getDefaultInstance()))
              .setSchemaDescriptor(new GestorAuditoriasMethodDescriptorSupplier("obtenerConsumosPeligrosos"))
              .build();
        }
      }
    }
    return getObtenerConsumosPeligrososMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GestorAuditoriasStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GestorAuditoriasStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GestorAuditoriasStub>() {
        @java.lang.Override
        public GestorAuditoriasStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GestorAuditoriasStub(channel, callOptions);
        }
      };
    return GestorAuditoriasStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GestorAuditoriasBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GestorAuditoriasBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GestorAuditoriasBlockingStub>() {
        @java.lang.Override
        public GestorAuditoriasBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GestorAuditoriasBlockingStub(channel, callOptions);
        }
      };
    return GestorAuditoriasBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GestorAuditoriasFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GestorAuditoriasFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GestorAuditoriasFutureStub>() {
        @java.lang.Override
        public GestorAuditoriasFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GestorAuditoriasFutureStub(channel, callOptions);
        }
      };
    return GestorAuditoriasFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     * <pre>
     * Server Streaming: 1 Petición -&gt; Flujo de Respuestas
     * </pre>
     */
    default void obtenerConsumosPeligrosos(grpc.AuditoriaOuterClass.AuditoriaRequest request,
        io.grpc.stub.StreamObserver<grpc.AuditoriaOuterClass.AuditoriaReply> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getObtenerConsumosPeligrososMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service GestorAuditorias.
   */
  public static abstract class GestorAuditoriasImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return GestorAuditoriasGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service GestorAuditorias.
   */
  public static final class GestorAuditoriasStub
      extends io.grpc.stub.AbstractAsyncStub<GestorAuditoriasStub> {
    private GestorAuditoriasStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GestorAuditoriasStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GestorAuditoriasStub(channel, callOptions);
    }

    /**
     * <pre>
     * Server Streaming: 1 Petición -&gt; Flujo de Respuestas
     * </pre>
     */
    public void obtenerConsumosPeligrosos(grpc.AuditoriaOuterClass.AuditoriaRequest request,
        io.grpc.stub.StreamObserver<grpc.AuditoriaOuterClass.AuditoriaReply> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getObtenerConsumosPeligrososMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service GestorAuditorias.
   */
  public static final class GestorAuditoriasBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<GestorAuditoriasBlockingStub> {
    private GestorAuditoriasBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GestorAuditoriasBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GestorAuditoriasBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Server Streaming: 1 Petición -&gt; Flujo de Respuestas
     * </pre>
     */
    public java.util.Iterator<grpc.AuditoriaOuterClass.AuditoriaReply> obtenerConsumosPeligrosos(
        grpc.AuditoriaOuterClass.AuditoriaRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getObtenerConsumosPeligrososMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service GestorAuditorias.
   */
  public static final class GestorAuditoriasFutureStub
      extends io.grpc.stub.AbstractFutureStub<GestorAuditoriasFutureStub> {
    private GestorAuditoriasFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GestorAuditoriasFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GestorAuditoriasFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_OBTENER_CONSUMOS_PELIGROSOS = 0;

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
        case METHODID_OBTENER_CONSUMOS_PELIGROSOS:
          serviceImpl.obtenerConsumosPeligrosos((grpc.AuditoriaOuterClass.AuditoriaRequest) request,
              (io.grpc.stub.StreamObserver<grpc.AuditoriaOuterClass.AuditoriaReply>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getObtenerConsumosPeligrososMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              grpc.AuditoriaOuterClass.AuditoriaRequest,
              grpc.AuditoriaOuterClass.AuditoriaReply>(
                service, METHODID_OBTENER_CONSUMOS_PELIGROSOS)))
        .build();
  }

  private static abstract class GestorAuditoriasBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GestorAuditoriasBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return grpc.AuditoriaOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GestorAuditorias");
    }
  }

  private static final class GestorAuditoriasFileDescriptorSupplier
      extends GestorAuditoriasBaseDescriptorSupplier {
    GestorAuditoriasFileDescriptorSupplier() {}
  }

  private static final class GestorAuditoriasMethodDescriptorSupplier
      extends GestorAuditoriasBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    GestorAuditoriasMethodDescriptorSupplier(String methodName) {
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
      synchronized (GestorAuditoriasGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GestorAuditoriasFileDescriptorSupplier())
              .addMethod(getObtenerConsumosPeligrososMethod())
              .build();
        }
      }
    }
    return result;
  }
}
