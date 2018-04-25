/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/guest/newspace/Error report/hdk/htc/lib2/WeatherLib2/src/com/htc/lib2/weather/ILocationService.aidl
 */
package com.htc.lib2.weather;
public interface ILocationService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.htc.lib2.weather.ILocationService
{
private static final java.lang.String DESCRIPTOR = "com.htc.lib2.weather.ILocationService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.htc.lib2.weather.ILocationService interface,
 * generating a proxy if needed.
 */
public static com.htc.lib2.weather.ILocationService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.htc.lib2.weather.ILocationService))) {
return ((com.htc.lib2.weather.ILocationService)iin);
}
return new com.htc.lib2.weather.ILocationService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getLastKnownLocation:
{
data.enforceInterface(DESCRIPTOR);
android.location.Location _result = this.getLastKnownLocation();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getLastKnownLocationByProvider:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
android.location.Location _result = this.getLastKnownLocationByProvider(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_requestSingleUpdate:
{
data.enforceInterface(DESCRIPTOR);
android.location.Location _result = this.requestSingleUpdate();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_getWifiScanResults:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
java.util.List<android.os.Bundle> _result = this.getWifiScanResults(_arg0);
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_requestLocationUpdate:
{
data.enforceInterface(DESCRIPTOR);
com.htc.lib2.weather.ILocationCallback _arg0;
_arg0 = com.htc.lib2.weather.ILocationCallback.Stub.asInterface(data.readStrongBinder());
this.requestLocationUpdate(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_requestLocationUpdateDetail:
{
data.enforceInterface(DESCRIPTOR);
com.htc.lib2.weather.ILocationCallback _arg0;
_arg0 = com.htc.lib2.weather.ILocationCallback.Stub.asInterface(data.readStrongBinder());
long _arg1;
_arg1 = data.readLong();
float _arg2;
_arg2 = data.readFloat();
android.location.Criteria _arg3;
if ((0!=data.readInt())) {
_arg3 = android.location.Criteria.CREATOR.createFromParcel(data);
}
else {
_arg3 = null;
}
this.requestLocationUpdateDetail(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_removeLocationUpdate:
{
data.enforceInterface(DESCRIPTOR);
com.htc.lib2.weather.ILocationCallback _arg0;
_arg0 = com.htc.lib2.weather.ILocationCallback.Stub.asInterface(data.readStrongBinder());
this.removeLocationUpdate(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_removeLocationUpdateDetail:
{
data.enforceInterface(DESCRIPTOR);
com.htc.lib2.weather.ILocationCallback _arg0;
_arg0 = com.htc.lib2.weather.ILocationCallback.Stub.asInterface(data.readStrongBinder());
this.removeLocationUpdateDetail(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_requestLocationUpdateByCriteria:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
float _arg1;
_arg1 = data.readFloat();
android.location.Criteria _arg2;
if ((0!=data.readInt())) {
_arg2 = android.location.Criteria.CREATOR.createFromParcel(data);
}
else {
_arg2 = null;
}
android.app.PendingIntent _arg3;
if ((0!=data.readInt())) {
_arg3 = android.app.PendingIntent.CREATOR.createFromParcel(data);
}
else {
_arg3 = null;
}
this.requestLocationUpdateByCriteria(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
case TRANSACTION_requestLocationUpdateByProvider:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
long _arg1;
_arg1 = data.readLong();
float _arg2;
_arg2 = data.readFloat();
android.app.PendingIntent _arg3;
if ((0!=data.readInt())) {
_arg3 = android.app.PendingIntent.CREATOR.createFromParcel(data);
}
else {
_arg3 = null;
}
this.requestLocationUpdateByProvider(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.htc.lib2.weather.ILocationService
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
@Override public android.location.Location getLastKnownLocation() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.location.Location _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLastKnownLocation, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.location.Location.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.location.Location getLastKnownLocationByProvider(java.lang.String provider) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.location.Location _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(provider);
mRemote.transact(Stub.TRANSACTION_getLastKnownLocationByProvider, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.location.Location.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public android.location.Location requestSingleUpdate() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.location.Location _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_requestSingleUpdate, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.location.Location.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.util.List<android.os.Bundle> getWifiScanResults(int level) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<android.os.Bundle> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(level);
mRemote.transact(Stub.TRANSACTION_getWifiScanResults, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(android.os.Bundle.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void requestLocationUpdate(com.htc.lib2.weather.ILocationCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_requestLocationUpdate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void requestLocationUpdateDetail(com.htc.lib2.weather.ILocationCallback cb, long minTime, float minDistance, android.location.Criteria criteria) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
_data.writeLong(minTime);
_data.writeFloat(minDistance);
if ((criteria!=null)) {
_data.writeInt(1);
criteria.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_requestLocationUpdateDetail, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void removeLocationUpdate(com.htc.lib2.weather.ILocationCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_removeLocationUpdate, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void removeLocationUpdateDetail(com.htc.lib2.weather.ILocationCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_removeLocationUpdateDetail, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void requestLocationUpdateByCriteria(long minTime, float minDistance, android.location.Criteria criteria, android.app.PendingIntent intent) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(minTime);
_data.writeFloat(minDistance);
if ((criteria!=null)) {
_data.writeInt(1);
criteria.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
if ((intent!=null)) {
_data.writeInt(1);
intent.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_requestLocationUpdateByCriteria, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void requestLocationUpdateByProvider(java.lang.String provider, long minTime, float minDistance, android.app.PendingIntent intent) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(provider);
_data.writeLong(minTime);
_data.writeFloat(minDistance);
if ((intent!=null)) {
_data.writeInt(1);
intent.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_requestLocationUpdateByProvider, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getLastKnownLocation = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getLastKnownLocationByProvider = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_requestSingleUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getWifiScanResults = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_requestLocationUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_requestLocationUpdateDetail = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_removeLocationUpdate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_removeLocationUpdateDetail = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_requestLocationUpdateByCriteria = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
static final int TRANSACTION_requestLocationUpdateByProvider = (android.os.IBinder.FIRST_CALL_TRANSACTION + 9);
}
public android.location.Location getLastKnownLocation() throws android.os.RemoteException;
public android.location.Location getLastKnownLocationByProvider(java.lang.String provider) throws android.os.RemoteException;
public android.location.Location requestSingleUpdate() throws android.os.RemoteException;
public java.util.List<android.os.Bundle> getWifiScanResults(int level) throws android.os.RemoteException;
public void requestLocationUpdate(com.htc.lib2.weather.ILocationCallback cb) throws android.os.RemoteException;
public void requestLocationUpdateDetail(com.htc.lib2.weather.ILocationCallback cb, long minTime, float minDistance, android.location.Criteria criteria) throws android.os.RemoteException;
public void removeLocationUpdate(com.htc.lib2.weather.ILocationCallback cb) throws android.os.RemoteException;
public void removeLocationUpdateDetail(com.htc.lib2.weather.ILocationCallback cb) throws android.os.RemoteException;
public void requestLocationUpdateByCriteria(long minTime, float minDistance, android.location.Criteria criteria, android.app.PendingIntent intent) throws android.os.RemoteException;
public void requestLocationUpdateByProvider(java.lang.String provider, long minTime, float minDistance, android.app.PendingIntent intent) throws android.os.RemoteException;
}
