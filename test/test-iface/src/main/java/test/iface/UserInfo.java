/**
 * Autogenerated by Thrift Compiler (0.13.0)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package test.iface;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked", "unused"})
@javax.annotation.Generated(value = "Autogenerated by Thrift Compiler (0.13.0)", date = "2020-05-28")
public class UserInfo implements org.apache.thrift.TBase<UserInfo, UserInfo._Fields>, java.io.Serializable, Cloneable, Comparable<UserInfo> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("UserInfo");

  private static final org.apache.thrift.protocol.TField USERNAME_FIELD_DESC = new org.apache.thrift.protocol.TField("username", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField PASSWORD_FIELD_DESC = new org.apache.thrift.protocol.TField("password", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField AVATAR_URL_FIELD_DESC = new org.apache.thrift.protocol.TField("avatarUrl", org.apache.thrift.protocol.TType.STRING, (short)3);

  private static final org.apache.thrift.scheme.SchemeFactory STANDARD_SCHEME_FACTORY = new UserInfoStandardSchemeFactory();
  private static final org.apache.thrift.scheme.SchemeFactory TUPLE_SCHEME_FACTORY = new UserInfoTupleSchemeFactory();

  public @org.apache.thrift.annotation.Nullable String username; // required
  public @org.apache.thrift.annotation.Nullable String password; // required
  public @org.apache.thrift.annotation.Nullable String avatarUrl; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    USERNAME((short)1, "username"),
    PASSWORD((short)2, "password"),
    AVATAR_URL((short)3, "avatarUrl");

    private static final java.util.Map<String, _Fields> byName = new java.util.HashMap<String, _Fields>();

    static {
      for (_Fields field : java.util.EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // USERNAME
          return USERNAME;
        case 2: // PASSWORD
          return PASSWORD;
        case 3: // AVATAR_URL
          return AVATAR_URL;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    @org.apache.thrift.annotation.Nullable
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  public static final java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    java.util.Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new java.util.EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.USERNAME, new org.apache.thrift.meta_data.FieldMetaData("username", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PASSWORD, new org.apache.thrift.meta_data.FieldMetaData("password", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.AVATAR_URL, new org.apache.thrift.meta_data.FieldMetaData("avatarUrl", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = java.util.Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(UserInfo.class, metaDataMap);
  }

  public UserInfo() {
  }

  public UserInfo(
    String username,
    String password,
    String avatarUrl)
  {
    this();
    this.username = username;
    this.password = password;
    this.avatarUrl = avatarUrl;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public UserInfo(UserInfo other) {
    if (other.isSetUsername()) {
      this.username = other.username;
    }
    if (other.isSetPassword()) {
      this.password = other.password;
    }
    if (other.isSetAvatarUrl()) {
      this.avatarUrl = other.avatarUrl;
    }
  }

  public UserInfo deepCopy() {
    return new UserInfo(this);
  }

  @Override
  public void clear() {
    this.username = null;
    this.password = null;
    this.avatarUrl = null;
  }

  @org.apache.thrift.annotation.Nullable
  public String getUsername() {
    return this.username;
  }

  public UserInfo setUsername(@org.apache.thrift.annotation.Nullable String username) {
    this.username = username;
    return this;
  }

  public void unsetUsername() {
    this.username = null;
  }

  /** Returns true if field username is set (has been assigned a value) and false otherwise */
  public boolean isSetUsername() {
    return this.username != null;
  }

  public void setUsernameIsSet(boolean value) {
    if (!value) {
      this.username = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public String getPassword() {
    return this.password;
  }

  public UserInfo setPassword(@org.apache.thrift.annotation.Nullable String password) {
    this.password = password;
    return this;
  }

  public void unsetPassword() {
    this.password = null;
  }

  /** Returns true if field password is set (has been assigned a value) and false otherwise */
  public boolean isSetPassword() {
    return this.password != null;
  }

  public void setPasswordIsSet(boolean value) {
    if (!value) {
      this.password = null;
    }
  }

  @org.apache.thrift.annotation.Nullable
  public String getAvatarUrl() {
    return this.avatarUrl;
  }

  public UserInfo setAvatarUrl(@org.apache.thrift.annotation.Nullable String avatarUrl) {
    this.avatarUrl = avatarUrl;
    return this;
  }

  public void unsetAvatarUrl() {
    this.avatarUrl = null;
  }

  /** Returns true if field avatarUrl is set (has been assigned a value) and false otherwise */
  public boolean isSetAvatarUrl() {
    return this.avatarUrl != null;
  }

  public void setAvatarUrlIsSet(boolean value) {
    if (!value) {
      this.avatarUrl = null;
    }
  }

  public void setFieldValue(_Fields field, @org.apache.thrift.annotation.Nullable Object value) {
    switch (field) {
    case USERNAME:
      if (value == null) {
        unsetUsername();
      } else {
        setUsername((String)value);
      }
      break;

    case PASSWORD:
      if (value == null) {
        unsetPassword();
      } else {
        setPassword((String)value);
      }
      break;

    case AVATAR_URL:
      if (value == null) {
        unsetAvatarUrl();
      } else {
        setAvatarUrl((String)value);
      }
      break;

    }
  }

  @org.apache.thrift.annotation.Nullable
  public Object getFieldValue(_Fields field) {
    switch (field) {
    case USERNAME:
      return getUsername();

    case PASSWORD:
      return getPassword();

    case AVATAR_URL:
      return getAvatarUrl();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case USERNAME:
      return isSetUsername();
    case PASSWORD:
      return isSetPassword();
    case AVATAR_URL:
      return isSetAvatarUrl();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof UserInfo)
      return this.equals((UserInfo)that);
    return false;
  }

  public boolean equals(UserInfo that) {
    if (that == null)
      return false;
    if (this == that)
      return true;

    boolean this_present_username = true && this.isSetUsername();
    boolean that_present_username = true && that.isSetUsername();
    if (this_present_username || that_present_username) {
      if (!(this_present_username && that_present_username))
        return false;
      if (!this.username.equals(that.username))
        return false;
    }

    boolean this_present_password = true && this.isSetPassword();
    boolean that_present_password = true && that.isSetPassword();
    if (this_present_password || that_present_password) {
      if (!(this_present_password && that_present_password))
        return false;
      if (!this.password.equals(that.password))
        return false;
    }

    boolean this_present_avatarUrl = true && this.isSetAvatarUrl();
    boolean that_present_avatarUrl = true && that.isSetAvatarUrl();
    if (this_present_avatarUrl || that_present_avatarUrl) {
      if (!(this_present_avatarUrl && that_present_avatarUrl))
        return false;
      if (!this.avatarUrl.equals(that.avatarUrl))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int hashCode = 1;

    hashCode = hashCode * 8191 + ((isSetUsername()) ? 131071 : 524287);
    if (isSetUsername())
      hashCode = hashCode * 8191 + username.hashCode();

    hashCode = hashCode * 8191 + ((isSetPassword()) ? 131071 : 524287);
    if (isSetPassword())
      hashCode = hashCode * 8191 + password.hashCode();

    hashCode = hashCode * 8191 + ((isSetAvatarUrl()) ? 131071 : 524287);
    if (isSetAvatarUrl())
      hashCode = hashCode * 8191 + avatarUrl.hashCode();

    return hashCode;
  }

  @Override
  public int compareTo(UserInfo other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetUsername()).compareTo(other.isSetUsername());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUsername()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.username, other.username);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPassword()).compareTo(other.isSetPassword());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPassword()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.password, other.password);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAvatarUrl()).compareTo(other.isSetAvatarUrl());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAvatarUrl()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.avatarUrl, other.avatarUrl);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  @org.apache.thrift.annotation.Nullable
  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    scheme(iprot).read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    scheme(oprot).write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("UserInfo(");
    boolean first = true;

    sb.append("username:");
    if (this.username == null) {
      sb.append("null");
    } else {
      sb.append(this.username);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("password:");
    if (this.password == null) {
      sb.append("null");
    } else {
      sb.append(this.password);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("avatarUrl:");
    if (this.avatarUrl == null) {
      sb.append("null");
    } else {
      sb.append(this.avatarUrl);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class UserInfoStandardSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public UserInfoStandardScheme getScheme() {
      return new UserInfoStandardScheme();
    }
  }

  private static class UserInfoStandardScheme extends org.apache.thrift.scheme.StandardScheme<UserInfo> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, UserInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // USERNAME
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.username = iprot.readString();
              struct.setUsernameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // PASSWORD
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.password = iprot.readString();
              struct.setPasswordIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // AVATAR_URL
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.avatarUrl = iprot.readString();
              struct.setAvatarUrlIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, UserInfo struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.username != null) {
        oprot.writeFieldBegin(USERNAME_FIELD_DESC);
        oprot.writeString(struct.username);
        oprot.writeFieldEnd();
      }
      if (struct.password != null) {
        oprot.writeFieldBegin(PASSWORD_FIELD_DESC);
        oprot.writeString(struct.password);
        oprot.writeFieldEnd();
      }
      if (struct.avatarUrl != null) {
        oprot.writeFieldBegin(AVATAR_URL_FIELD_DESC);
        oprot.writeString(struct.avatarUrl);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class UserInfoTupleSchemeFactory implements org.apache.thrift.scheme.SchemeFactory {
    public UserInfoTupleScheme getScheme() {
      return new UserInfoTupleScheme();
    }
  }

  private static class UserInfoTupleScheme extends org.apache.thrift.scheme.TupleScheme<UserInfo> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, UserInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol oprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet optionals = new java.util.BitSet();
      if (struct.isSetUsername()) {
        optionals.set(0);
      }
      if (struct.isSetPassword()) {
        optionals.set(1);
      }
      if (struct.isSetAvatarUrl()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetUsername()) {
        oprot.writeString(struct.username);
      }
      if (struct.isSetPassword()) {
        oprot.writeString(struct.password);
      }
      if (struct.isSetAvatarUrl()) {
        oprot.writeString(struct.avatarUrl);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, UserInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TTupleProtocol iprot = (org.apache.thrift.protocol.TTupleProtocol) prot;
      java.util.BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        struct.username = iprot.readString();
        struct.setUsernameIsSet(true);
      }
      if (incoming.get(1)) {
        struct.password = iprot.readString();
        struct.setPasswordIsSet(true);
      }
      if (incoming.get(2)) {
        struct.avatarUrl = iprot.readString();
        struct.setAvatarUrlIsSet(true);
      }
    }
  }

  private static <S extends org.apache.thrift.scheme.IScheme> S scheme(org.apache.thrift.protocol.TProtocol proto) {
    return (org.apache.thrift.scheme.StandardScheme.class.equals(proto.getScheme()) ? STANDARD_SCHEME_FACTORY : TUPLE_SCHEME_FACTORY).getScheme();
  }
}

