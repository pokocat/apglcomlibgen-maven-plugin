package com.ericsson.jcat;

public class AttributeInfo {
	private final String mName;
	private boolean mMandatory = false;
	private boolean mRestricted = false;
	private boolean mReadonly = false;
	// private final boolean mInitialValue;
	// private final boolean mApplicationTag;
	// private String mRangeMax = "0" only for derivedDataTypeRef;
	// private String mRangeMin = "0"only for derivedDataTypeRef;
	// private String mLengthRangeMax = "0"only for derivedDataTypeRef;
	// private String mLengthRangeMin = "0"only for derivedDataTypeRef;
	// private final String mValidValues only for derivedDataTypeRef;
	// private final boolean mAlphabet;
	// private final boolean mLocal;
	private boolean mNotification = true;
	private boolean mPersistent = true;
	// private final boolean mIsStatic;
	// private final boolean mUndefined;
	// private final boolean mGenerateXml;
	// private final boolean mGenerateEmptyDefaultValue;
	// private final boolean mVisibility;
	private boolean mKey = false;
	private final AttributeDataType mDataType;

	public enum AttributeDataType {
		isString, isStruct, isDerivedDataType
	}

	public AttributeInfo(String name, boolean manda, boolean restri, boolean readonly, boolean notification,
			boolean persistent, boolean key, AttributeDataType dataType) {
		mName = name;
		mMandatory = manda;
		mRestricted = restri;
		mReadonly = readonly;
		// mInitialValue = initialValue;
		// mApplicationTag = applicationTag;
		// mRangeMax = rangeMax;
		// mRangeMin = rangeMin;
		// mLengthRangeMax = lengthRangeMax;
		// mLengthRangeMin = lengthRangeMin;
		// mValidValues = validValues;
		// mAlphabet = alphabet;
		// mLocal = local;
		mNotification = notification;
		mPersistent = persistent;
		// mIsStatic = isStatic;
		// mUndefined = undefined;
		// mGenerateXml = generateXml;
		// mGenerateEmptyDefaultValue = generateEmptyDefaultValue;
		// mVisibility = visibility;
		mKey = key;
		mDataType = dataType;
	}

	public String getAttributeName() {
		return mName;
	}

	public boolean getMandatory() {
		return mMandatory;
	}

	public boolean getRestricted() {
		return mRestricted;
	}

	public boolean getReadonly() {
		return mReadonly;
	}

	public boolean getNotification() {
		return mNotification;
	}

	public boolean getPersistent() {
		return mPersistent;
	}

	public boolean getKey() {
		return mKey;
	}

	public AttributeDataType getDataType() {
		return mDataType;
	}

}
