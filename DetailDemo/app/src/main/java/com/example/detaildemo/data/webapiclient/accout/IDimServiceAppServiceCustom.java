package com.example.detaildemo.data.webapiclient.accout;


/**
 * サービスアプリ向け共通ID管理アプリ外部インタフェース.
 *
 * 共通ID管理アプリがサービスアプリ向けに公開している外部インタフェースです.
 * 全てのインタフェースは非同期で実行され、呼出し後、サービスアプリに即座に処理を返却します.
 * この時、各メソッドの戻り値が処理中を表す場合、共通ID管理アプリは非同期で各処理を実施しています.
 * 非同期で実行された処理が完了すると、各インタフェースのcallback引数に指定されたコールバックインタフェースの所定のメソッドを呼び出します.
 */
public interface IDimServiceAppServiceCustom extends android.os.IInterface
{
    /** Default implementation for IDimServiceAppServiceCustom. */
    public static class Default implements IDimServiceAppServiceCustom
    {
        /**
         * サービス登録要求(通知先設定あり).
         *
         * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
         * @param serviceKey サービス識別キー.登録対象サービスのサービス識別キー.
         * @param setDefIdReceiver デフォルトID設定通知を受信するComponent名.
         * @param userAuthReceiver ユーザ認証通知を受信するComponent名.
         * @param deleteIdReceiver ID解除通知を受信するComponent名.
         * @param invalidateIdReceiver ID認証状態無効化通知を受信するComponent名.
         * @param callback コールバックサービス.チェックが完了するとここで指定された
         * コールバックインタフェースのonCompleteRegistServiceが呼び出されます.
         * @return 要求受付結果.
         * @throws android.os.RemoteException
         */
        @Override public int registServiceWithReceiver(int appReqId, java.lang.String serviceKey, java.lang.String setDefIdReceiver, java.lang.String userAuthReceiver, java.lang.String deleteIdReceiver, java.lang.String invalidateIdReceiver, IDimServiceAppCallbacks callback) throws android.os.RemoteException
        {
            return 0;
        }
        @Override
        public android.os.IBinder asBinder() {
            return null;
        }
    }
    /** Local-side IPC implementation stub class. */
    public static abstract class Stub extends android.os.Binder implements IDimServiceAppServiceCustom
    {
        private static final java.lang.String DESCRIPTOR = "com.nttdocomo.android.idmanager.IDimServiceAppServiceCustom";
        /** Construct the stub at attach it to the interface. */
        public Stub()
        {
            this.attachInterface(this, DESCRIPTOR);
        }
        /**
         * Cast an IBinder object into an com.nttdocomo.android.idmanager.IDimServiceAppServiceCustom interface,
         * generating a proxy if needed.
         */
        public static IDimServiceAppServiceCustom asInterface(android.os.IBinder obj)
        {
            if ((obj==null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin!=null)&&(iin instanceof IDimServiceAppServiceCustom))) {
                return ((IDimServiceAppServiceCustom)iin);
            }
            return new IDimServiceAppServiceCustom.Stub.Proxy(obj);
        }
        @Override public android.os.IBinder asBinder()
        {
            return this;
        }
        @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
        {
            java.lang.String descriptor = DESCRIPTOR;
            switch (code)
            {
                case INTERFACE_TRANSACTION:
                {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_registServiceWithReceiver:
                {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    java.lang.String _arg1;
                    _arg1 = data.readString();
                    java.lang.String _arg2;
                    _arg2 = data.readString();
                    java.lang.String _arg3;
                    _arg3 = data.readString();
                    java.lang.String _arg4;
                    _arg4 = data.readString();
                    java.lang.String _arg5;
                    _arg5 = data.readString();
                    IDimServiceAppCallbacks _arg6;
                    _arg6 = IDimServiceAppCallbacks.Stub.asInterface(data.readStrongBinder());
                    int _result = this.registServiceWithReceiver(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                default:
                {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }
        private static class Proxy implements IDimServiceAppServiceCustom
        {
            private android.os.IBinder mRemote;
            Proxy(android.os.IBinder remote)
            {
                mRemote = remote;
            }
            @Override public android.os.IBinder asBinder()
            {
                return mRemote;
            }
            public java.lang.String getInterfaceDescriptor()
            {
                return DESCRIPTOR;
            }
            /**
             * サービス登録要求(通知先設定あり).
             *
             * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
             * @param serviceKey サービス識別キー.登録対象サービスのサービス識別キー.
             * @param setDefIdReceiver デフォルトID設定通知を受信するComponent名.
             * @param userAuthReceiver ユーザ認証通知を受信するComponent名.
             * @param deleteIdReceiver ID解除通知を受信するComponent名.
             * @param invalidateIdReceiver ID認証状態無効化通知を受信するComponent名.
             * @param callback コールバックサービス.チェックが完了するとここで指定された
             * コールバックインタフェースのonCompleteRegistServiceが呼び出されます.
             * @return 要求受付結果.
             * @throws android.os.RemoteException
             */
            @Override public int registServiceWithReceiver(int appReqId, java.lang.String serviceKey, java.lang.String setDefIdReceiver, java.lang.String userAuthReceiver, java.lang.String deleteIdReceiver, java.lang.String invalidateIdReceiver, IDimServiceAppCallbacks callback) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(appReqId);
                    _data.writeString(serviceKey);
                    _data.writeString(setDefIdReceiver);
                    _data.writeString(userAuthReceiver);
                    _data.writeString(deleteIdReceiver);
                    _data.writeString(invalidateIdReceiver);
                    _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
                    boolean _status = mRemote.transact(Stub.TRANSACTION_registServiceWithReceiver, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().registServiceWithReceiver(appReqId, serviceKey, setDefIdReceiver, userAuthReceiver, deleteIdReceiver, invalidateIdReceiver, callback);
                    }
                    _reply.readException();
                    _result = _reply.readInt();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
                return _result;
            }
            public static IDimServiceAppServiceCustom sDefaultImpl;
        }
        static final int TRANSACTION_registServiceWithReceiver = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        public static boolean setDefaultImpl(IDimServiceAppServiceCustom impl) {
            if (Stub.Proxy.sDefaultImpl == null && impl != null) {
                Stub.Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }
        public static IDimServiceAppServiceCustom getDefaultImpl() {
            return Stub.Proxy.sDefaultImpl;
        }
    }
    /**
     * サービス登録要求(通知先設定あり).
     *
     * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
     * @param serviceKey サービス識別キー.登録対象サービスのサービス識別キー.
     * @param setDefIdReceiver デフォルトID設定通知を受信するComponent名.
     * @param userAuthReceiver ユーザ認証通知を受信するComponent名.
     * @param deleteIdReceiver ID解除通知を受信するComponent名.
     * @param invalidateIdReceiver ID認証状態無効化通知を受信するComponent名.
     * @param callback コールバックサービス.チェックが完了するとここで指定された
     * コールバックインタフェースのonCompleteRegistServiceが呼び出されます.
     * @return 要求受付結果.
     * @throws android.os.RemoteException
     */
    public int registServiceWithReceiver(int appReqId, java.lang.String serviceKey, java.lang.String setDefIdReceiver, java.lang.String userAuthReceiver, java.lang.String deleteIdReceiver, java.lang.String invalidateIdReceiver, IDimServiceAppCallbacks callback) throws android.os.RemoteException;
}

