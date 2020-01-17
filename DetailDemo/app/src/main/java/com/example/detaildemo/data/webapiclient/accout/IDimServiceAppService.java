package com.example.detaildemo.data.webapiclient.accout;
/**
 * サービスアプリ向け共通ID管理アプリ外部インタフェース.
 *
 * 共通ID管理アプリがサービスアプリ向けに公開している外部インタフェースです.
 * 全てのインタフェースは非同期で実行され、呼出し後、サービスアプリに即座に処理を返却します.
 * この時、各メソッドの戻り値が処理中を表す場合、共通ID管理アプリは非同期で各処理を実施しています.
 * 非同期で実行された処理が完了すると、各インタフェースのcallback引数に指定されたコールバックインタフェースの所定のメソッドを呼び出します.
 */
public interface IDimServiceAppService extends android.os.IInterface
{
    /** Default implementation for IDimServiceAppService. */
    public static class Default implements IDimServiceAppService
    {
        /**
         * サービス確認要求.
         *
         * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
         * @param serviceKey サービス識別キー.確認対象サービスのサービス識別キー.
         * @param callback コールバックサービス.チェックが完了するとここで指定された
         * コールバックインタフェースのonCompleteCheckServiceが呼び出されます.
         * @return 要求受付結果.
         * @throws android.os.RemoteException
         */
        @Override public int checkService(int appReqId, java.lang.String serviceKey, IDimServiceAppCallbacks callback) throws android.os.RemoteException
        {
            return 0;
        }
        /**
         * サービス登録要求(通知先設定なし).
         *
         * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
         * @param serviceKey サービス識別キー.登録対象サービスのサービス識別キー.
         * @param callback コールバックサービス.チェックが完了するとここで指定された
         * コールバックインタフェースのonCompleteRegistServiceが呼び出されます.
         * @return 要求受付結果.
         * @throws android.os.RemoteException
         */
        @Override public int registService(int appReqId, java.lang.String serviceKey, IDimServiceAppCallbacks callback) throws android.os.RemoteException
        {
            return 0;
        }
        /**
         * 認証トークン払い出し要求.
         *
         * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
         * @param id ID.トークン払い出し対象のID.
         * @param serviceKey サービス識別キー.認証トークンを利用するサービスのサービス識別キー.
         * @param option 再認証オプション.再認証動作を指定するパラメータ.
         * @param appCheckKey アプリチェックキー.2回目以降のトークン払い出し要求に付与する.
         * @param callback コールバックサービス.チェックが完了するとここで指定された
         * コールバックインタフェースのonCompleteGetAuthTokenが呼び出されます.
         * @return 要求受付結果.
         * @throws android.os.RemoteException
         */
        @Override public int getAuthToken(int appReqId, java.lang.String id, java.lang.String serviceKey, int option, java.lang.String appCheckKey, IDimServiceAppCallbacks callback) throws android.os.RemoteException
        {
            return 0;
        }
        /**
         * 個別ID状態問い合わせ.
         *
         * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
         * @param id ID.問い合わせ対象のID.
         * @param serviceKey サービス識別キー.問い合わせ元サービスのサービス識別キー.
         * @param tokenStatusCode token認証ステータス詳細コード.
         * @param callback コールバックサービス.チェックが完了するとここで指定された
         * コールバックインタフェースのonCompleteGetIdStatusが呼び出されます.
         * @return 要求受付結果.
         * @throws android.os.RemoteException
         */
        @Override public int getIdStatus(int appReqId, java.lang.String id, java.lang.String serviceKey, java.lang.String tokenStatusCode, IDimServiceAppCallbacks callback) throws android.os.RemoteException
        {
            return 0;
        }
        /**
         * ワンタイムトークン払い出し要求.
         *
         * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
         * @param id ID.ワンタイムトークン払い出し対象のID.
         * @param serviceKey サービス識別キー.ワンタイムトークンを利用するサービスのサービス識別キー.
         * @param option 再認証オプション.再認証動作を指定するパラメータ.
         * @param appCheckKey アプリチェックキー.2回目以降のトークン払い出し要求に付与する.
         * @param callback コールバックサービス.チェックが完了するとここで指定された
         * コールバックインタフェースのonCompleteGetOneTimePasswordが呼び出されます.
         * @return 要求受付結果.
         * @throws android.os.RemoteException
         */
        @Override public int getOneTimePassword(int appReqId, java.lang.String id, java.lang.String serviceKey, int option, java.lang.String appCheckKey, IDimServiceAppCallbacks callback) throws android.os.RemoteException
        {
            return 0;
        }
        /**
         * サービスアプリ向けID管理アップデート要求.
         *
         * @param protocolVer 依頼元サービスが認識しているID管理アプリのプロトコルバージョン.
         * @param serviceKey サービス識別キー.ID管理アプリのアップデートを要求するサービスのサービス識別キー.
         * @throws android.os.RemoteException
         */
        @Override public void updateIdManager(java.lang.String protocolVer, java.lang.String serviceKey) throws android.os.RemoteException
        {
        }
        @Override
        public android.os.IBinder asBinder() {
            return null;
        }
    }
    /** Local-side IPC implementation stub class. */
    public static abstract class Stub extends android.os.Binder implements IDimServiceAppService
    {
        private static final java.lang.String DESCRIPTOR = "IDimServiceAppService";
        /** Construct the stub at attach it to the interface. */
        public Stub()
        {
            this.attachInterface(this, DESCRIPTOR);
        }
        /**
         * Cast an IBinder object into an IDimServiceAppService interface,
         * generating a proxy if needed.
         */
        public static IDimServiceAppService asInterface(android.os.IBinder obj)
        {
            if ((obj==null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin!=null)&&(iin instanceof IDimServiceAppService))) {
                return ((IDimServiceAppService)iin);
            }
            return new IDimServiceAppService.Stub.Proxy(obj);
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
                case TRANSACTION_checkService:
                {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    java.lang.String _arg1;
                    _arg1 = data.readString();
                    IDimServiceAppCallbacks _arg2;
                    _arg2 = IDimServiceAppCallbacks.Stub.asInterface(data.readStrongBinder());
                    int _result = this.checkService(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_registService:
                {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    java.lang.String _arg1;
                    _arg1 = data.readString();
                    IDimServiceAppCallbacks _arg2;
                    _arg2 = IDimServiceAppCallbacks.Stub.asInterface(data.readStrongBinder());
                    int _result = this.registService(_arg0, _arg1, _arg2);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_getAuthToken:
                {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    java.lang.String _arg1;
                    _arg1 = data.readString();
                    java.lang.String _arg2;
                    _arg2 = data.readString();
                    int _arg3;
                    _arg3 = data.readInt();
                    java.lang.String _arg4;
                    _arg4 = data.readString();
                    IDimServiceAppCallbacks _arg5;
                    _arg5 = IDimServiceAppCallbacks.Stub.asInterface(data.readStrongBinder());
                    int _result = this.getAuthToken(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_getIdStatus:
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
                    IDimServiceAppCallbacks _arg4;
                    _arg4 = IDimServiceAppCallbacks.Stub.asInterface(data.readStrongBinder());
                    int _result = this.getIdStatus(_arg0, _arg1, _arg2, _arg3, _arg4);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_getOneTimePassword:
                {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    java.lang.String _arg1;
                    _arg1 = data.readString();
                    java.lang.String _arg2;
                    _arg2 = data.readString();
                    int _arg3;
                    _arg3 = data.readInt();
                    java.lang.String _arg4;
                    _arg4 = data.readString();
                    IDimServiceAppCallbacks _arg5;
                    _arg5 = IDimServiceAppCallbacks.Stub.asInterface(data.readStrongBinder());
                    int _result = this.getOneTimePassword(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                    reply.writeNoException();
                    reply.writeInt(_result);
                    return true;
                }
                case TRANSACTION_updateIdManager:
                {
                    data.enforceInterface(descriptor);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    java.lang.String _arg1;
                    _arg1 = data.readString();
                    this.updateIdManager(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                default:
                {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }
        private static class Proxy implements IDimServiceAppService
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
             * サービス確認要求.
             *
             * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
             * @param serviceKey サービス識別キー.確認対象サービスのサービス識別キー.
             * @param callback コールバックサービス.チェックが完了するとここで指定された
             * コールバックインタフェースのonCompleteCheckServiceが呼び出されます.
             * @return 要求受付結果.
             * @throws android.os.RemoteException
             */
            @Override public int checkService(int appReqId, java.lang.String serviceKey, IDimServiceAppCallbacks callback) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(appReqId);
                    _data.writeString(serviceKey);
                    _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
                    boolean _status = mRemote.transact(Stub.TRANSACTION_checkService, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().checkService(appReqId, serviceKey, callback);
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
            /**
             * サービス登録要求(通知先設定なし).
             *
             * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
             * @param serviceKey サービス識別キー.登録対象サービスのサービス識別キー.
             * @param callback コールバックサービス.チェックが完了するとここで指定された
             * コールバックインタフェースのonCompleteRegistServiceが呼び出されます.
             * @return 要求受付結果.
             * @throws android.os.RemoteException
             */
            @Override public int registService(int appReqId, java.lang.String serviceKey, IDimServiceAppCallbacks callback) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(appReqId);
                    _data.writeString(serviceKey);
                    _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
                    boolean _status = mRemote.transact(Stub.TRANSACTION_registService, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().registService(appReqId, serviceKey, callback);
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
            /**
             * 認証トークン払い出し要求.
             *
             * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
             * @param id ID.トークン払い出し対象のID.
             * @param serviceKey サービス識別キー.認証トークンを利用するサービスのサービス識別キー.
             * @param option 再認証オプション.再認証動作を指定するパラメータ.
             * @param appCheckKey アプリチェックキー.2回目以降のトークン払い出し要求に付与する.
             * @param callback コールバックサービス.チェックが完了するとここで指定された
             * コールバックインタフェースのonCompleteGetAuthTokenが呼び出されます.
             * @return 要求受付結果.
             * @throws android.os.RemoteException
             */
            @Override public int getAuthToken(int appReqId, java.lang.String id, java.lang.String serviceKey, int option, java.lang.String appCheckKey, IDimServiceAppCallbacks callback) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(appReqId);
                    _data.writeString(id);
                    _data.writeString(serviceKey);
                    _data.writeInt(option);
                    _data.writeString(appCheckKey);
                    _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
                    boolean _status = mRemote.transact(Stub.TRANSACTION_getAuthToken, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().getAuthToken(appReqId, id, serviceKey, option, appCheckKey, callback);
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
            /**
             * 個別ID状態問い合わせ.
             *
             * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
             * @param id ID.問い合わせ対象のID.
             * @param serviceKey サービス識別キー.問い合わせ元サービスのサービス識別キー.
             * @param tokenStatusCode token認証ステータス詳細コード.
             * @param callback コールバックサービス.チェックが完了するとここで指定された
             * コールバックインタフェースのonCompleteGetIdStatusが呼び出されます.
             * @return 要求受付結果.
             * @throws android.os.RemoteException
             */
            @Override public int getIdStatus(int appReqId, java.lang.String id, java.lang.String serviceKey, java.lang.String tokenStatusCode, IDimServiceAppCallbacks callback) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(appReqId);
                    _data.writeString(id);
                    _data.writeString(serviceKey);
                    _data.writeString(tokenStatusCode);
                    _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
                    boolean _status = mRemote.transact(Stub.TRANSACTION_getIdStatus, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().getIdStatus(appReqId, id, serviceKey, tokenStatusCode, callback);
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
            /**
             * ワンタイムトークン払い出し要求.
             *
             * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
             * @param id ID.ワンタイムトークン払い出し対象のID.
             * @param serviceKey サービス識別キー.ワンタイムトークンを利用するサービスのサービス識別キー.
             * @param option 再認証オプション.再認証動作を指定するパラメータ.
             * @param appCheckKey アプリチェックキー.2回目以降のトークン払い出し要求に付与する.
             * @param callback コールバックサービス.チェックが完了するとここで指定された
             * コールバックインタフェースのonCompleteGetOneTimePasswordが呼び出されます.
             * @return 要求受付結果.
             * @throws android.os.RemoteException
             */
            @Override public int getOneTimePassword(int appReqId, java.lang.String id, java.lang.String serviceKey, int option, java.lang.String appCheckKey, IDimServiceAppCallbacks callback) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                int _result;
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(appReqId);
                    _data.writeString(id);
                    _data.writeString(serviceKey);
                    _data.writeInt(option);
                    _data.writeString(appCheckKey);
                    _data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
                    boolean _status = mRemote.transact(Stub.TRANSACTION_getOneTimePassword, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        return getDefaultImpl().getOneTimePassword(appReqId, id, serviceKey, option, appCheckKey, callback);
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
            /**
             * サービスアプリ向けID管理アップデート要求.
             *
             * @param protocolVer 依頼元サービスが認識しているID管理アプリのプロトコルバージョン.
             * @param serviceKey サービス識別キー.ID管理アプリのアップデートを要求するサービスのサービス識別キー.
             * @throws android.os.RemoteException
             */
            @Override public void updateIdManager(java.lang.String protocolVer, java.lang.String serviceKey) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(protocolVer);
                    _data.writeString(serviceKey);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_updateIdManager, _data, _reply, 0);
                    if (!_status && getDefaultImpl() != null) {
                        getDefaultImpl().updateIdManager(protocolVer, serviceKey);
                        return;
                    }
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            public static IDimServiceAppService sDefaultImpl;
        }
        static final int TRANSACTION_checkService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_registService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_getAuthToken = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
        static final int TRANSACTION_getIdStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
        static final int TRANSACTION_getOneTimePassword = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
        static final int TRANSACTION_updateIdManager = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
        public static boolean setDefaultImpl(IDimServiceAppService impl) {
            if (Stub.Proxy.sDefaultImpl == null && impl != null) {
                Stub.Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }
        public static IDimServiceAppService getDefaultImpl() {
            return Stub.Proxy.sDefaultImpl;
        }
    }
    /**
     * サービス確認要求.
     *
     * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
     * @param serviceKey サービス識別キー.確認対象サービスのサービス識別キー.
     * @param callback コールバックサービス.チェックが完了するとここで指定された
     * コールバックインタフェースのonCompleteCheckServiceが呼び出されます.
     * @return 要求受付結果.
     * @throws android.os.RemoteException
     */
    public int checkService(int appReqId, java.lang.String serviceKey, IDimServiceAppCallbacks callback) throws android.os.RemoteException;
    /**
     * サービス登録要求(通知先設定なし).
     *
     * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
     * @param serviceKey サービス識別キー.登録対象サービスのサービス識別キー.
     * @param callback コールバックサービス.チェックが完了するとここで指定された
     * コールバックインタフェースのonCompleteRegistServiceが呼び出されます.
     * @return 要求受付結果.
     * @throws android.os.RemoteException
     */
    public int registService(int appReqId, java.lang.String serviceKey, IDimServiceAppCallbacks callback) throws android.os.RemoteException;
    /**
     * 認証トークン払い出し要求.
     *
     * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
     * @param id ID.トークン払い出し対象のID.
     * @param serviceKey サービス識別キー.認証トークンを利用するサービスのサービス識別キー.
     * @param option 再認証オプション.再認証動作を指定するパラメータ.
     * @param appCheckKey アプリチェックキー.2回目以降のトークン払い出し要求に付与する.
     * @param callback コールバックサービス.チェックが完了するとここで指定された
     * コールバックインタフェースのonCompleteGetAuthTokenが呼び出されます.
     * @return 要求受付結果.
     * @throws android.os.RemoteException
     */
    public int getAuthToken(int appReqId, java.lang.String id, java.lang.String serviceKey, int option, java.lang.String appCheckKey, IDimServiceAppCallbacks callback) throws android.os.RemoteException;
    /**
     * 個別ID状態問い合わせ.
     *
     * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
     * @param id ID.問い合わせ対象のID.
     * @param serviceKey サービス識別キー.問い合わせ元サービスのサービス識別キー.
     * @param tokenStatusCode token認証ステータス詳細コード.
     * @param callback コールバックサービス.チェックが完了するとここで指定された
     * コールバックインタフェースのonCompleteGetIdStatusが呼び出されます.
     * @return 要求受付結果.
     * @throws android.os.RemoteException
     */
    public int getIdStatus(int appReqId, java.lang.String id, java.lang.String serviceKey, java.lang.String tokenStatusCode, IDimServiceAppCallbacks callback) throws android.os.RemoteException;
    /**
     * ワンタイムトークン払い出し要求.
     *
     * @param appReqId アプリ要求ID.コールバック時にここで指定された値を引き渡します.
     * @param id ID.ワンタイムトークン払い出し対象のID.
     * @param serviceKey サービス識別キー.ワンタイムトークンを利用するサービスのサービス識別キー.
     * @param option 再認証オプション.再認証動作を指定するパラメータ.
     * @param appCheckKey アプリチェックキー.2回目以降のトークン払い出し要求に付与する.
     * @param callback コールバックサービス.チェックが完了するとここで指定された
     * コールバックインタフェースのonCompleteGetOneTimePasswordが呼び出されます.
     * @return 要求受付結果.
     * @throws android.os.RemoteException
     */
    public int getOneTimePassword(int appReqId, java.lang.String id, java.lang.String serviceKey, int option, java.lang.String appCheckKey, IDimServiceAppCallbacks callback) throws android.os.RemoteException;
    /**
     * サービスアプリ向けID管理アップデート要求.
     *
     * @param protocolVer 依頼元サービスが認識しているID管理アプリのプロトコルバージョン.
     * @param serviceKey サービス識別キー.ID管理アプリのアップデートを要求するサービスのサービス識別キー.
     * @throws android.os.RemoteException
     */
    public void updateIdManager(java.lang.String protocolVer, java.lang.String serviceKey) throws android.os.RemoteException;
}

