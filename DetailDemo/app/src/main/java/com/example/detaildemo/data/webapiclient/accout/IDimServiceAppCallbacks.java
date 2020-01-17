package com.example.detaildemo.data.webapiclient.accout;


/**
 * サービスアプリ向け共通ID管理アプリ外部コールバックインタフェース.
 *
 * 共通ID管理アプリが処理結果を通知する為にサービスアプリ側に実装が必要なインタフェースです.
 * 共通ID管理アプリは処理が完了すると、各処理に対応したここで定義するインタフェースを呼び出してサービスアプリに処理の完了と結果を通知します.
 */
public interface IDimServiceAppCallbacks extends android.os.IInterface
{
    /** Default implementation for IDimServiceAppCallbacks. */
    public static class Default implements IDimServiceAppCallbacks
    {
        /**
         * サービス確認完了.
         *
         * @param appReqId アプリ要求ID.checkService呼び出し時に指定した値が設定されます.
         * @param result 処理結果.
         * @param version アプリバージョン.
         * @param protocolVersion プロトコルバージョン.
         * @throws android.os.RemoteException
         */
        @Override public void onCompleteCheckService(int appReqId, int result, java.lang.String version, java.lang.String protocolVersion) throws android.os.RemoteException
        {
        }
        /**
         * サービス登録完了.
         *
         * @param appReqId アプリ要求ID.registService呼び出し時に指定した値が設定されます.
         * @param result 処理結果.
         * @throws android.os.RemoteException
         */
        @Override public void onCompleteRegistService(int appReqId, int result) throws android.os.RemoteException
        {
        }
        /**
         * 認証トークン払い出し完了.
         *
         * @param appReqId アプリ要求ID.getAuthToken呼び出し時に指定した値が設定されます.
         * @param result 処理結果.
         * @param id ID.トークン払い出し対象のID.
         * @param token 認証トークン.サービスサーバに向けて送信するための認証トークン.
         * @param appCheckKey アプリチェックキー.入力にアプリチェックキーがない場合に返却.
         * @throws android.os.RemoteException
         */
        @Override public void onCompleteGetAuthToken(int appReqId, int result, java.lang.String id, java.lang.String token, java.lang.String appCheckKey) throws android.os.RemoteException
        {
        }
        /**
         * 個別ID状態問い合わせ完了.
         *
         * @param appReqId アプリ要求ID.getIdStatus呼び出し時に指定した値が設定されます.
         * @param result 処理結果.
         * @param id 問い合わせ対象のID.
         * @param isDefault idがデフォルトIDとして機能する場合はtrue、それ以外はfalse.
         * @param hasMsn msnにMSN情報が設定されている場合はtrue、それ以外はfalse.
         * @param authStatus idの認証状態が有効である場合はtrue、無効である場合はfalse.
         * @throws android.os.RemoteException
         */
        @Override public void onCompleteGetIdStatus(int appReqId, int result, java.lang.String id, boolean isDefault, boolean hasMsn, boolean authStatus) throws android.os.RemoteException
        {
        }
        /**
         * ワンタイムトークン払い出し完了.
         *
         * @param appReqId アプリ要求ID.getOneTimePassword呼び出し時に指定した値が設定されます.
         * @param result 処理結果.
         * @param id ワンタイムトークン払い出し対象のID.
         * @param oneTimePassword ワンタイムトークン.
         * @param appCheckKey アプリチェックキー.入力にアプリチェックキーがない場合に返却.
         * @throws android.os.RemoteException
         */
        @Override public void onCompleteGetOneTimePassword(int appReqId, int result, java.lang.String id, java.lang.String oneTimePassword, java.lang.String appCheckKey) throws android.os.RemoteException
        {
        }
        @Override
        public android.os.IBinder asBinder() {
            return null;
        }
    }
    /** Local-side IPC implementation stub class. */
    public static abstract class Stub extends android.os.Binder implements IDimServiceAppCallbacks
    {
        private static final java.lang.String DESCRIPTOR = "com.nttdocomo.android.idmanager.IDimServiceAppCallbacks";
        /** Construct the stub at attach it to the interface. */
        public Stub()
        {
            this.attachInterface(this, DESCRIPTOR);
        }
        /**
         * Cast an IBinder object into an IDimServiceAppCallbacks interface,
         * generating a proxy if needed.
         */
        public static IDimServiceAppCallbacks asInterface(android.os.IBinder obj)
        {
            if ((obj==null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin!=null)&&(iin instanceof IDimServiceAppCallbacks))) {
                return ((IDimServiceAppCallbacks)iin);
            }
            return new IDimServiceAppCallbacks.Stub.Proxy(obj);
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
                case TRANSACTION_onCompleteCheckService:
                {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    int _arg1;
                    _arg1 = data.readInt();
                    java.lang.String _arg2;
                    _arg2 = data.readString();
                    java.lang.String _arg3;
                    _arg3 = data.readString();
                    this.onCompleteCheckService(_arg0, _arg1, _arg2, _arg3);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_onCompleteRegistService:
                {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    int _arg1;
                    _arg1 = data.readInt();
                    this.onCompleteRegistService(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_onCompleteGetAuthToken:
                {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    int _arg1;
                    _arg1 = data.readInt();
                    java.lang.String _arg2;
                    _arg2 = data.readString();
                    java.lang.String _arg3;
                    _arg3 = data.readString();
                    java.lang.String _arg4;
                    _arg4 = data.readString();
                    this.onCompleteGetAuthToken(_arg0, _arg1, _arg2, _arg3, _arg4);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_onCompleteGetIdStatus:
                {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    int _arg1;
                    _arg1 = data.readInt();
                    java.lang.String _arg2;
                    _arg2 = data.readString();
                    boolean _arg3;
                    _arg3 = (0!=data.readInt());
                    boolean _arg4;
                    _arg4 = (0!=data.readInt());
                    boolean _arg5;
                    _arg5 = (0!=data.readInt());
                    this.onCompleteGetIdStatus(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_onCompleteGetOneTimePassword:
                {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    int _arg1;
                    _arg1 = data.readInt();
                    java.lang.String _arg2;
                    _arg2 = data.readString();
                    java.lang.String _arg3;
                    _arg3 = data.readString();
                    java.lang.String _arg4;
                    _arg4 = data.readString();
                    this.onCompleteGetOneTimePassword(_arg0, _arg1, _arg2, _arg3, _arg4);
                    reply.writeNoException();
                    return true;
                }
                default:
                {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }
        private static class Proxy implements IDimServiceAppCallbacks
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
             * サービス確認完了.
             *
             * @param appReqId アプリ要求ID.checkService呼び出し時に指定した値が設定されます.
             * @param result 処理結果.
             * @param version アプリバージョン.
             * @param protocolVersion プロトコルバージョン.
             * @throws android.os.RemoteException
             */
            @Override public void onCompleteCheckService(int appReqId, int result, java.lang.String version, java.lang.String protocolVersion) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(appReqId);
                    _data.writeInt(result);
                    _data.writeString(version);
                    _data.writeString(protocolVersion);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_onCompleteCheckService, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().onCompleteCheckService(appReqId, result, version, protocolVersion);
                        return;
                    }
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            /**
             * サービス登録完了.
             *
             * @param appReqId アプリ要求ID.registService呼び出し時に指定した値が設定されます.
             * @param result 処理結果.
             * @throws android.os.RemoteException
             */
            @Override public void onCompleteRegistService(int appReqId, int result) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(appReqId);
                    _data.writeInt(result);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_onCompleteRegistService, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().onCompleteRegistService(appReqId, result);
                        return;
                    }
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            /**
             * 認証トークン払い出し完了.
             *
             * @param appReqId アプリ要求ID.getAuthToken呼び出し時に指定した値が設定されます.
             * @param result 処理結果.
             * @param id ID.トークン払い出し対象のID.
             * @param token 認証トークン.サービスサーバに向けて送信するための認証トークン.
             * @param appCheckKey アプリチェックキー.入力にアプリチェックキーがない場合に返却.
             * @throws android.os.RemoteException
             */
            @Override public void onCompleteGetAuthToken(int appReqId, int result, java.lang.String id, java.lang.String token, java.lang.String appCheckKey) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(appReqId);
                    _data.writeInt(result);
                    _data.writeString(id);
                    _data.writeString(token);
                    _data.writeString(appCheckKey);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_onCompleteGetAuthToken, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().onCompleteGetAuthToken(appReqId, result, id, token, appCheckKey);
                        return;
                    }
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            /**
             * 個別ID状態問い合わせ完了.
             *
             * @param appReqId アプリ要求ID.getIdStatus呼び出し時に指定した値が設定されます.
             * @param result 処理結果.
             * @param id 問い合わせ対象のID.
             * @param isDefault idがデフォルトIDとして機能する場合はtrue、それ以外はfalse.
             * @param hasMsn msnにMSN情報が設定されている場合はtrue、それ以外はfalse.
             * @param authStatus idの認証状態が有効である場合はtrue、無効である場合はfalse.
             * @throws android.os.RemoteException
             */
            @Override public void onCompleteGetIdStatus(int appReqId, int result, java.lang.String id, boolean isDefault, boolean hasMsn, boolean authStatus) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(appReqId);
                    _data.writeInt(result);
                    _data.writeString(id);
                    _data.writeInt(((isDefault)?(1):(0)));
                    _data.writeInt(((hasMsn)?(1):(0)));
                    _data.writeInt(((authStatus)?(1):(0)));
                    boolean _status = mRemote.transact(Stub.TRANSACTION_onCompleteGetIdStatus, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().onCompleteGetIdStatus(appReqId, result, id, isDefault, hasMsn, authStatus);
                        return;
                    }
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            /**
             * ワンタイムトークン払い出し完了.
             *
             * @param appReqId アプリ要求ID.getOneTimePassword呼び出し時に指定した値が設定されます.
             * @param result 処理結果.
             * @param id ワンタイムトークン払い出し対象のID.
             * @param oneTimePassword ワンタイムトークン.
             * @param appCheckKey アプリチェックキー.入力にアプリチェックキーがない場合に返却.
             * @throws android.os.RemoteException
             */
            @Override public void onCompleteGetOneTimePassword(int appReqId, int result, java.lang.String id, java.lang.String oneTimePassword, java.lang.String appCheckKey) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(appReqId);
                    _data.writeInt(result);
                    _data.writeString(id);
                    _data.writeString(oneTimePassword);
                    _data.writeString(appCheckKey);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_onCompleteGetOneTimePassword, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().onCompleteGetOneTimePassword(appReqId, result, id, oneTimePassword, appCheckKey);
                        return;
                    }
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            public static IDimServiceAppCallbacks sDefaultImpl;
        }
        static final int TRANSACTION_onCompleteCheckService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_onCompleteRegistService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_onCompleteGetAuthToken = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
        static final int TRANSACTION_onCompleteGetIdStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
        static final int TRANSACTION_onCompleteGetOneTimePassword = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
        public static boolean setDefaultImpl(IDimServiceAppCallbacks impl) {
            if (Stub.Proxy.sDefaultImpl == null && impl != null) {
                Stub.Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }
        public static IDimServiceAppCallbacks getDefaultImpl() {
            return Stub.Proxy.sDefaultImpl;
        }
    }
    /**
     * サービス確認完了.
     *
     * @param appReqId アプリ要求ID.checkService呼び出し時に指定した値が設定されます.
     * @param result 処理結果.
     * @param version アプリバージョン.
     * @param protocolVersion プロトコルバージョン.
     * @throws android.os.RemoteException
     */
    public void onCompleteCheckService(int appReqId, int result, java.lang.String version, java.lang.String protocolVersion) throws android.os.RemoteException;
    /**
     * サービス登録完了.
     *
     * @param appReqId アプリ要求ID.registService呼び出し時に指定した値が設定されます.
     * @param result 処理結果.
     * @throws android.os.RemoteException
     */
    public void onCompleteRegistService(int appReqId, int result) throws android.os.RemoteException;
    /**
     * 認証トークン払い出し完了.
     *
     * @param appReqId アプリ要求ID.getAuthToken呼び出し時に指定した値が設定されます.
     * @param result 処理結果.
     * @param id ID.トークン払い出し対象のID.
     * @param token 認証トークン.サービスサーバに向けて送信するための認証トークン.
     * @param appCheckKey アプリチェックキー.入力にアプリチェックキーがない場合に返却.
     * @throws android.os.RemoteException
     */
    public void onCompleteGetAuthToken(int appReqId, int result, java.lang.String id, java.lang.String token, java.lang.String appCheckKey) throws android.os.RemoteException;
    /**
     * 個別ID状態問い合わせ完了.
     *
     * @param appReqId アプリ要求ID.getIdStatus呼び出し時に指定した値が設定されます.
     * @param result 処理結果.
     * @param id 問い合わせ対象のID.
     * @param isDefault idがデフォルトIDとして機能する場合はtrue、それ以外はfalse.
     * @param hasMsn msnにMSN情報が設定されている場合はtrue、それ以外はfalse.
     * @param authStatus idの認証状態が有効である場合はtrue、無効である場合はfalse.
     * @throws android.os.RemoteException
     */
    public void onCompleteGetIdStatus(int appReqId, int result, java.lang.String id, boolean isDefault, boolean hasMsn, boolean authStatus) throws android.os.RemoteException;
    /**
     * ワンタイムトークン払い出し完了.
     *
     * @param appReqId アプリ要求ID.getOneTimePassword呼び出し時に指定した値が設定されます.
     * @param result 処理結果.
     * @param id ワンタイムトークン払い出し対象のID.
     * @param oneTimePassword ワンタイムトークン.
     * @param appCheckKey アプリチェックキー.入力にアプリチェックキーがない場合に返却.
     * @throws android.os.RemoteException
     */
    public void onCompleteGetOneTimePassword(int appReqId, int result, java.lang.String id, java.lang.String oneTimePassword, java.lang.String appCheckKey) throws android.os.RemoteException;
}

