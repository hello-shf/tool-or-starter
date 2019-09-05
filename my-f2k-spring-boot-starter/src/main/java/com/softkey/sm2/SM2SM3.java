package com.softkey.sm2;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.SecureRandom;


public class SM2SM3 {
	private static final int BYTE_LENGTH = 32;
	private static final int BLOCK_LENGTH = 64;
	private static final int BUFFER_LENGTH = 64*2;

	private static final int SM2_ADDBYTE = 97;//加密后的数据会增加的长度
	private static final int MAX_ENCLEN = 128; //最大的加密长度分组
	private static final int MAX_DECLEN = (MAX_ENCLEN + SM2_ADDBYTE); //最大的解密长度分组

	private byte[] xBuf = new byte[BUFFER_LENGTH];
	private int xBufOff;
	private byte[] V= SM3.iv;
	private int cntBlock = 0;


	public static BigInteger ecc_p;
	public static BigInteger ecc_a;
	public static BigInteger ecc_b;
	public static BigInteger ecc_n;
	public static BigInteger ecc_gx;
	public static BigInteger ecc_gy;

	public static ECCurve ecc_curve;
	public static ECPoint ecc_point_g;

	public static ECDomainParameters ecc_bc_spec;

	public SM2SM3() {

	}

	@SuppressWarnings("deprecation")
	public static ECKeyPairGenerator GenKeyPair() {

		ECKeyPairGenerator ecc_key_pair_generator;
		ECFieldElement ecc_gx_fieldelement;
		ECFieldElement ecc_gy_fieldelement;


		ecc_gx_fieldelement = new ECFieldElement.Fp( Util.p, Util.Gx);
		ecc_gy_fieldelement = new ECFieldElement.Fp( Util.p, Util.Gy);

		ecc_curve = new ECCurve.Fp(Util.p, Util.a,  Util.b);
		ecc_point_g = new ECPoint.Fp(ecc_curve, ecc_gx_fieldelement, ecc_gy_fieldelement);

		ecc_bc_spec = new ECDomainParameters(ecc_curve, ecc_point_g, Util.n);

		ECKeyGenerationParameters ecc_ecgenparam;
		ecc_ecgenparam = new ECKeyGenerationParameters(ecc_bc_spec, new SecureRandom());

		ecc_key_pair_generator = new ECKeyPairGenerator();
		ecc_key_pair_generator.init(ecc_ecgenparam);

		return  ecc_key_pair_generator;
	}


	public SM2SM3(SM2SM3 t) {
		//System.arraycopy(t.X, 0, X, 0, t.X.length);
		xBufOff = t.xBufOff;
		cntBlock=t.cntBlock;
		System.arraycopy(t.xBuf, 0, xBuf, 0, t.xBuf.length);
		System.arraycopy(t.V, 0, V, 0, t.V.length);
	}


	public static byte[] GetE(byte[] z, byte[] HashMsgValue)
	{
		SM2SM3 digest = new SM2SM3();

		digest.update(z, 0, z.length);

		digest.update(HashMsgValue, 0, 32);

		byte[] md = new byte[32];
		digest.doFinal(md, 0);
		return md;

	}

	public static byte[] GetMsgHash(String msg)
	{
		SM2SM3 digest = new SM2SM3();
		byte[] p = msg.getBytes();
		digest.update(p, 0, p.length);

		byte[] md = new byte[32];
		digest.doFinal(md, 0);
		return md;

	}

	public  static boolean YtVerfiy(String id, String InString, String PubKeyX,String PubKeyY,String VerfiySign )
	{

		SM2SM3 digest = new SM2SM3();
		BigInteger affineX = new BigInteger(PubKeyX, 16);
		BigInteger affineY = new BigInteger(PubKeyY, 16);

		byte[] z =  digest.Sm2GetZ(affineX, affineY, id.getBytes());

		byte []MsgHash=GetMsgHash(InString);

		byte []E=GetE(z,MsgHash);

		BigInteger r = new BigInteger(VerfiySign.substring(0, 64), 16);
		BigInteger s = new BigInteger(VerfiySign.substring(64, 128), 16);

		return subSm2Verify(E,affineX,affineY, r, s );


	}

	@SuppressWarnings("deprecation")
	private static boolean subSm2Verify(byte[] md,  BigInteger PubKeyX, BigInteger PubKeyY,BigInteger r, BigInteger s)
	{
		SM2Result sm2Ret= new SM2Result();;
		ECFieldElement ecc_gx_fieldelement;
		ECFieldElement ecc_gy_fieldelement;

		ecc_gx_fieldelement = new ECFieldElement.Fp(Util.p, Util.Gx);
		ecc_gy_fieldelement = new ECFieldElement.Fp(Util.p,  Util.Gy);

		ecc_curve = new ECCurve.Fp(Util.p, Util.a, Util.b);
		ecc_point_g=new ECPoint.Fp(ecc_curve, ecc_gx_fieldelement, ecc_gy_fieldelement);

		ECFieldElement ecc_kx_fieldelement = new ECFieldElement.Fp(Util.p, PubKeyX);
		ECFieldElement ecc_ky_fieldelement = new ECFieldElement.Fp(Util.p, PubKeyY);
		ECPoint userKey = new ECPoint.Fp(ecc_curve, ecc_kx_fieldelement, ecc_ky_fieldelement);

		sm2Ret.R = null;

		// e_
		BigInteger e = new BigInteger(1, md);
		// t
		BigInteger t = r.add(s).mod(Util.n);

		if (t.equals(BigInteger.ZERO))
			return false;

		// x1y1
		ECPoint x1y1 = ecc_point_g.multiply(s);
		x1y1 = x1y1.add(userKey.multiply(t));

		// R
		sm2Ret.R = e.add(x1y1.getY().toBigInteger()).mod(Util.n);

		if (r.equals(sm2Ret.R))
		{
			return true;
		}
		return false;
	}

	private static String myhex(byte indata)
	{
		String outstring;
		outstring=String.format("%X",indata);
		if(outstring.length()<2)outstring="0"+outstring;
		return outstring;
	}


	@SuppressWarnings("deprecation")
	private static void  sub_EncBufBySoft(byte[] InBuf,  byte[] OubBuf,int InBuflen,  BigInteger PubKeyX, BigInteger PubKeyY,ECKeyPairGenerator ecc_key_pair_generator)
	{

		int n;
		byte[] data = new byte[InBuflen];
		for (n = 0; n < InBuflen; n++)
		{
			data[n] = InBuf[n];
		}

		Cipher cipher = new Cipher();
		ECPoint c1 = cipher.Init_enc(PubKeyX,PubKeyY,ecc_key_pair_generator);
		byte[] bc1 = c1.getEncoded();
		int c1_len = bc1.length;

		cipher.Encrypt(data);

		byte[] c3 = new byte[32];
		cipher.Dofinal(c3);

		for (n = 0; n < c1_len; n++)
		{
			OubBuf[n] = bc1[n];
		}
		for (n = 0; n < InBuflen; n++)
		{
			OubBuf[n + c1_len] = data[n];
		}
		for (n = 0; n < 32; n++)
		{
			OubBuf[n + c1_len + InBuflen] = c3[n];
		}
	}

	public static String SM2_EncStringBySoft(String InString,  String PubKeyX, String PubKeyY)
	{
		ECKeyPairGenerator ecc_key_pair_generator= SM2SM3.GenKeyPair();
		String OutString;
		BigInteger affineX = new BigInteger(PubKeyX, 16);
		BigInteger affineY = new BigInteger(PubKeyY, 16);

		int n, incount = 0, outcount = 0;
		int inlen=InString.getBytes().length+1;
		byte[] InBuf =new byte[inlen];
		System.arraycopy(InString.getBytes(), 0, InBuf, 0, InString.getBytes().length);
		byte[] temp_InBuf = new byte[MAX_ENCLEN+ SM2_ADDBYTE], temp_OutBuf = new byte[MAX_ENCLEN + SM2_ADDBYTE];
		int outlen = (inlen / MAX_ENCLEN + 1) * SM2_ADDBYTE + inlen;
		byte[] OutBuf = new byte[outlen];
		int temp_inlen;
		while (inlen > 0)
		{
			if (inlen > MAX_ENCLEN)
				temp_inlen = MAX_ENCLEN;
			else
				temp_inlen = inlen;
			for (n = 0; n < temp_inlen; n++)
			{
				temp_InBuf[n] = InBuf[incount + n];
			}
			sub_EncBufBySoft(temp_InBuf, temp_OutBuf, temp_inlen, affineX,affineY,ecc_key_pair_generator);
			for (n = 0; n < (temp_inlen + SM2_ADDBYTE); n++)
			{
				OutBuf[outcount + n] = temp_OutBuf[n];
			}
			inlen = inlen - MAX_ENCLEN;
			incount = incount + MAX_ENCLEN;
			outcount = outcount + MAX_DECLEN;
		}

		OutString = "";
		for (n = 0 ;n<= outlen - 1;n++)
		{
			OutString = OutString +myhex(OutBuf[n]) ;
		}
		return OutString;


	}


	/**
	 * SM3结果输出
	 * @param out 保存SM3结构的缓冲区
	 * @param outOff 缓冲区偏移量
	 * @return
	 */
	public int doFinal(byte[] out, int outOff) {
		byte[] tmp = doFinal();
		System.arraycopy(tmp, 0, out, 0, tmp.length);
		return BYTE_LENGTH;
	}

	public String getAlgorithmName() {
		return "SM3";
	}

	public int getDigestSize() {
		return BYTE_LENGTH;
	}

	public void reset() {
		xBufOff = 0;
		cntBlock = 0;
		V = SM3.iv;
	}


	/**
	 * 明文输入
	 * @param in 明文输入缓冲区
	 * @param inOff 缓冲区偏移量
	 * @param len 明文长度
	 */
	public void update(byte[] in, int inOff, int len) {
		if(xBufOff+len > BUFFER_LENGTH) {
			int tmpLen = xBufOff+len-BUFFER_LENGTH;
			System.arraycopy(in, inOff, xBuf, xBufOff, BUFFER_LENGTH-xBufOff);
			doUpdate();
			xBufOff = 0;
			int i=1;
			while(tmpLen > BUFFER_LENGTH) {
				tmpLen -= BUFFER_LENGTH;
				System.arraycopy(in, inOff+BUFFER_LENGTH*i, xBuf, xBufOff, BUFFER_LENGTH-xBufOff);
				doUpdate();
				xBufOff = 0;
				i++;
			}
			System.arraycopy(in, inOff+len-tmpLen, xBuf, xBufOff, tmpLen);
			xBufOff += tmpLen;

		} else if(xBufOff+len == BUFFER_LENGTH) {
			System.arraycopy(in, inOff, xBuf, xBufOff, len);
			doUpdate();
			xBufOff = 0;
		} else {
			System.arraycopy(in, inOff, xBuf, xBufOff, len);
			xBufOff += len;
		}
	}

	public void doUpdate() {
		byte[] B = new byte[BLOCK_LENGTH];
		for(int i=0; i<BUFFER_LENGTH; i +=BLOCK_LENGTH) {
			System.arraycopy(xBuf, i, B, 0, B.length);
			doHash(B);
		}
		cntBlock += BUFFER_LENGTH/BLOCK_LENGTH;
	}


	public void doUpdateEx(byte InB) {
		byte[] B = new byte[1];
		B[0]=InB;
		System.arraycopy(B, 0, xBuf, xBufOff,1);
		doUpdate();
		xBufOff = 0;
	}

	public void doHash(byte[] B) {
		V = SM3.CF(V, B);
	}

	private byte[] doFinal() {
		byte[] B = new byte[BLOCK_LENGTH];
		byte[] buffer = new byte[xBufOff];
		System.arraycopy(xBuf, 0, buffer, 0, buffer.length);
		byte[] tmp = SM3.padding(buffer, cntBlock);
		for(int i=0; i<tmp.length; i +=BLOCK_LENGTH) {
			System.arraycopy(tmp, i, B, 0, B.length);
			doHash(B);
			cntBlock++;
		}

		return V;
	}

	private byte[] getSM2Za(byte[] x, byte[] y, byte[] id) {
		byte[] tmp = Util.IntToByte(id.length*8);
		byte[] buffer = new byte[2];
		buffer[0] = tmp[1];
		buffer[1] = tmp[0];
		byte[] a = Util.getA();
		byte[] b = Util.getB();
		byte[] gx = Util.getGx();
		byte[] gy = Util.getGy();


		SM2SM3 digest = new SM2SM3();
		digest.update( buffer,0,2);


		digest.update( id,0, id.length );

		digest.update(  a,0, a.length );

		digest.update(  b,0, b.length );

		digest.update(  gx,0, gx.length );

		digest.update(  gy,0, gy.length );

		digest.update(  x,0, x.length );

		digest.update(  y,0, y.length );

		byte[] out = new byte[32];
		digest.doFinal(out, 0);
		return out;




	}


	public byte[] Sm2GetZ(BigInteger PubKeyX, BigInteger PubKeyY, byte[] id) {
		byte[] x = Util.asUnsigned32ByteArray(PubKeyX);
		byte[] y = Util.asUnsigned32ByteArray(PubKeyY);
		byte[] tmp = getSM2Za(x, y, id);
		reset();
		return tmp;

	}
}

class SM3 {
	public static final byte[] iv = new BigInteger("7380166f4914b2b9172442d7da8a0600a96f30bc163138aae38dee4db0fb0e4e", 16).toByteArray();
	public static int[] Tj = new int[64];
	static {
		for(int i=0; i<16; i++) {
			Tj[i] = 0x79cc4519;
		}
		for(int i=16; i<64; i++) {
			Tj[i] = 0x7a879d8a;
		}
	}

	public static byte[] CF(byte[] V, byte[] B) {
		int[] v, b;
		v = convert(V);
		b = convert(B);

		return convert(CF(v, b));
	}

	private static int[] convert(byte[] arr) {
		int[] out = new int[arr.length/4];
		byte[] tmp = new byte[4];
		for(int i=0; i<arr.length; i += 4) {
			System.arraycopy(arr, i, tmp, 0, 4);
			out[i/4] = bigEndianByteToInt(tmp);
		}

		return out;
	}

	private static byte[] convert(int[] arr) {
		byte[] out = new byte[arr.length*4];
		byte[] tmp = null;
		for(int i=0; i<arr.length; i++) {
			tmp = bigEndianIntToByte(arr[i]);
			System.arraycopy(tmp, 0, out, i*4, 4);
		}

		return out;
	}

	public static int[] CF(int[] V, int[] B) {
		int a, b, c, d, e, f, g, h;
		int ss1, ss2, tt1, tt2;
		a = V[0];
		b = V[1];
		c = V[2];
		d = V[3];
		e = V[4];
		f = V[5];
		g = V[6];
		h = V[7];
		/*System.out.print("  ");
		System.out.print(Integer.toHexString(a)+" ");
		System.out.print(Integer.toHexString(b)+" ");
		System.out.print(Integer.toHexString(c)+" ");
		System.out.print(Integer.toHexString(d)+" ");
		System.out.print(Integer.toHexString(e)+" ");
		System.out.print(Integer.toHexString(f)+" ");
		System.out.print(Integer.toHexString(g)+" ");
		System.out.print(Integer.toHexString(h)+" ");
		System.out.println();*/

		int[][] arr = expand(B);
		int[] w = arr[0];
		int[] w1 = arr[1];
		/*System.out.println("W");
		print(w);
		System.out.println("W1");
		print(w1);*/
		for(int j=0; j<64; j++) {
			ss1 = (bitCycleLeft(a, 12) + e + bitCycleLeft(Tj[j], j));
			ss1 = bitCycleLeft(ss1, 7);
			ss2 = ss1 ^ bitCycleLeft(a, 12);
			tt1 = FFj(a, b, c, j) + d + ss2 + w1[j];
			tt2 = GGj(e, f, g, j) + h + ss1 + w[j];
			d = c;
			c = bitCycleLeft(b, 9);
			b = a;
			a = tt1;
			h = g;
			g = bitCycleLeft(f, 19);
			f = e;
			e = P0(tt2);

			/*System.out.print(j+" ");
			System.out.print(Integer.toHexString(a)+" ");
			System.out.print(Integer.toHexString(b)+" ");
			System.out.print(Integer.toHexString(c)+" ");
			System.out.print(Integer.toHexString(d)+" ");
			System.out.print(Integer.toHexString(e)+" ");
			System.out.print(Integer.toHexString(f)+" ");
			System.out.print(Integer.toHexString(g)+" ");
			System.out.print(Integer.toHexString(h)+" ");
			System.out.println();*/
		}
		//System.out.println("*****************************************");

		int[] out = new int[8];
		out[0] = a ^ V[0];
		out[1] = b ^ V[1];
		out[2] = c ^ V[2];
		out[3] = d ^ V[3];
		out[4] = e ^ V[4];
		out[5] = f ^ V[5];
		out[6] = g ^ V[6];
		out[7] = h ^ V[7];

		return out;
	}

	private static int[][] expand(byte[] B) {
		int W[] = new int[68];
		int W1[] = new int[64];
		byte[] tmp = new byte[4];
		for(int i=0; i<B.length; i+=4) {
			for(int j=0; j<4; j++) {
				tmp[j] = B[i+j];
			}
			W[i/4] = bigEndianByteToInt(tmp);
		}

		for(int i=16; i<68; i++) {
			W[i] = P1(W[i-16] ^ W[i-9] ^ bitCycleLeft(W[i-3], 15)) ^ bitCycleLeft(W[i-13], 7) ^ W[i-6];
		}

		for(int i=0; i<64; i++) {
			W1[i] = W[i] ^ W[i+4];
		}

		int arr[][] = new int[][]{W, W1};

		return arr;
	}

	private static int[][] expand(int[] B) {
		return expand(convert(B));
	}

	private static byte[] bigEndianIntToByte(int num) {
		return back(Util.IntToByte(num));
	}

	private static int bigEndianByteToInt(byte[] bytes) {
		return Util.ByteToInt(back(bytes));
	}

	private static int FFj(int X, int Y, int Z, int j) {
		if(j>=0 && j<=15) {
			return FF1j(X, Y, Z);
		} else {
			return FF2j(X, Y, Z);
		}
	}
	private static int GGj(int X, int Y, int Z, int j) {
		if(j>=0 && j<=15) {
			return GG1j(X, Y, Z);
		} else {
			return GG2j(X, Y, Z);
		}
	}
	/***********************************************/
	// 逻辑位运算函数
	private static int FF1j(int X, int Y, int Z) {
		int tmp = X ^ Y ^ Z;

		return tmp;
	}

	private static int FF2j(int X, int Y, int Z) {
		int tmp = ( (X & Y) | (X & Z) | (Y & Z) );

		return tmp;
	}

	private static int GG1j(int X, int Y, int Z) {
		int tmp = X ^ Y ^ Z;

		return tmp;
	}

	private static int GG2j(int X, int Y, int Z) {
		int tmp = (X & Y) | (~X & Z) ;

		return tmp;
	}

	private static int P0(int X) {
		int t = X ^ bitCycleLeft(X, 9) ^ bitCycleLeft(X, 17);

		return t;
	}

	private static int P1(int X) {
		int t = X ^ bitCycleLeft(X, 15) ^ bitCycleLeft(X, 23);

		return t;
	}

	/**
	 * 对最后一个分组字节数据padding
	 * @param in
	 * @param bLen 分组个数
	 * @return
	 */
	public static byte[] padding(byte[] in, int bLen) {
		//第一bit为1 所以长度=8 * in.length+1 k为所补的bit k+1/8 为需要补的字节
		int k = 448 - (8 * in.length+1) % 512;
		if( k < 0) {
			k = 960 - (8 * in.length+1) % 512;
		}
		k += 1;
		byte[] padd = new byte[k/8];
		padd[0] = (byte)0x80;
		long n = in.length * 8+bLen*512;
		//64/8 字节 长度
		//k/8 字节padding
		byte[] out = new byte[in.length+k/8+64/8];
		int pos = 0;
		System.arraycopy(in, 0, out, 0, in.length);
		pos += in.length;
		System.arraycopy(padd, 0, out, pos, padd.length);
		pos += padd.length;
		byte[] tmp = back(Util.LongToByte(n));
		System.arraycopy(tmp, 0, out, pos, tmp.length);

		return out;
	}

	/**
	 * 字节数组逆序
	 * @param in
	 * @return
	 */
	private static byte[] back(byte[] in) {
		byte[] out = new byte[in.length];
		for(int i=0; i<out.length; i++) {
			out[i] = in[out.length-i-1];
		}

		return out;
	}

	private static int bitCycleLeft(int n, int bitLen) {
		bitLen %= 32;
		byte[] tmp = bigEndianIntToByte(n);
		int byteLen = bitLen / 8;
		int len = bitLen % 8;
		if( byteLen > 0) {
			tmp = byteCycleLeft(tmp, byteLen);
		}

		if(len > 0) {
			tmp = bitSmall8CycleLeft(tmp, len);
		}

		return bigEndianByteToInt(tmp);
	}

	private static byte[] bitSmall8CycleLeft(byte[] in, int len) {
		byte[] tmp = new byte[in.length];
		int t1, t2, t3;
		for(int i=0; i<tmp.length; i++) {
			t1 = (byte) ((in[i] & 0x000000ff)<<len);
			t2 = (byte) ((in[(i+1)%tmp.length] & 0x000000ff)>>(8-len));
			t3 = (byte) (t1 | t2);
			tmp[i] = (byte)t3;
		}


		return tmp;
	}

	private static byte[] byteCycleLeft(byte[] in, int byteLen) {
		byte[] tmp = new byte[in.length];
		System.arraycopy(in, byteLen, tmp, 0, in.length-byteLen);
		System.arraycopy(in, 0, tmp, in.length-byteLen, byteLen);

		return tmp;
	}

}

class Cipher
{
	private int ct = 1;

	private ECPoint p2;
	private SM2SM3 sm3keybase;
	private SM2SM3 sm3c3;

	private byte[] key = new byte[32];
	private byte keyOff = 0;

	public Cipher()
	{
	}


	public static byte[] GetMsgHash(String msg)
	{
		SM2SM3 digest = new SM2SM3();
		byte[] p = msg.getBytes();
		digest.update(p, 0, p.length);

		byte[] md = new byte[32];
		digest.doFinal(md, 0);
		return md;

	}

	@SuppressWarnings("deprecation")
	private void  Reset()//注意，加密使用无符号的数组转换，以便与硬件相一致
	{
		sm3keybase = new SM2SM3();
		sm3c3 = new SM2SM3();

		byte[] p;

		p = Util.asUnsigned32ByteArray(p2.getX().toBigInteger());
		sm3keybase.update(p, 0, p.length);
		sm3c3.update(p, 0, p.length);

		p = Util.asUnsigned32ByteArray(p2.getY().toBigInteger());
		sm3keybase.update(p, 0, p.length);
		ct = 1;
		NextKey();
	}

	private void  NextKey()
	{
		SM2SM3 sm3keycur = new SM2SM3(sm3keybase);

		byte[] p=new byte[4];

		byte temp;
		temp=(byte)(ct >> 24 & 0x00ff);
		p[0]=temp;
		temp=(byte)(ct >> 16 & 0x00ff);
		p[1]=temp;
		temp=(byte)(ct >> 8 & 0x00ff);
		p[2]=temp;
		temp=(byte)(ct & 0x00ff);
		p[3]=temp;
		sm3keycur.update(p, 0, p.length);
		sm3keycur.doFinal(key, 0);
		keyOff = 0;
		ct++;
	}

	@SuppressWarnings("deprecation")
	public ECPoint Init_enc(BigInteger PubKeyX, BigInteger PubKeyY,ECKeyPairGenerator ecc_key_pair_generator)
	{
		ECPoint c1=null;


		AsymmetricCipherKeyPair key = ecc_key_pair_generator.generateKeyPair();
		ECPrivateKeyParameters ecpriv = (ECPrivateKeyParameters) key.getPrivate();
		ECPublicKeyParameters ecpub = (ECPublicKeyParameters) key.getPublic();

		ECCurve curve = new ECCurve.Fp(Util.p, Util.a, Util.b);
		c1 = ecpub.getQ();

		BigInteger k=ecpriv.getD();

		ECFieldElement ecc_X = new ECFieldElement.Fp(Util.p, PubKeyX);
		ECFieldElement ecc_Y = new ECFieldElement.Fp(Util.p, PubKeyY);

		ECPoint userKey = new ECPoint.Fp(curve, ecc_X, ecc_Y);

		p2 = userKey.multiply(k);
		Reset();

		return c1;
	}

	public  void  Encrypt(byte[] data)
	{
		sm3c3.update(data, 0, data.length);
		for (int i = 0; i < data.length; i++)
		{
			if (keyOff == key.length)
				NextKey();

			data[i] ^= key[keyOff++];
		}
	}

	public void  Init_dec(BigInteger userD, ECPoint c1)
	{
		p2 = c1.multiply(userD);
		Reset();
	}

	public  void  Decrypt(byte[] data)
	{
		for (int i = 0; i < data.length; i++)
		{
			if (keyOff == key.length)
				NextKey();

			data[i] ^= key[keyOff++];
		}
		sm3c3.update(data, 0, data.length);
	}

	@SuppressWarnings("deprecation")
	public  void  Dofinal(byte[] c3)
	{
		byte[] p = Util.asUnsigned32ByteArray(p2.getY().toBigInteger());
		sm3c3.update(p, 0, p.length);
		sm3c3.doFinal(c3, 0);
		Reset();
	}


}

class Util {
	public static BigInteger p = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF", 16);
	public static BigInteger a = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC", 16);
	public static BigInteger b = new BigInteger("28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93", 16);
	public static BigInteger n = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123", 16);
	public static BigInteger Gx = new BigInteger("32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7", 16);
	public static BigInteger Gy = new BigInteger("BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0", 16);
	public static byte[] getP() {
		return p.toByteArray();
	}
	public static byte[] getA() {
		return asUnsigned32ByteArray(a);
	}
	public static byte[] getB() {
		return asUnsigned32ByteArray(b);
	}
	public static byte[] getN() {
		return asUnsigned32ByteArray(n);
	}
	public static byte[] getGx() {
		return asUnsigned32ByteArray(Gx);
	}
	public static byte[] getGy() {
		return asUnsigned32ByteArray(Gy);
	}

	/**
	 * 整形转换成网络传输的字节流（字节数组）型数据
	 * @param num 一个整型数据
	 * @return 4个字节的自己数组
	 */
	public static byte[] IntToByte(int num) {
		byte[] bytes = new byte[4];

		bytes[0] = (byte)(0xff&(num>>0));
		bytes[1] = (byte)(0xff&(num>>8));
		bytes[2] = (byte)(0xff&(num>>16));
		bytes[3] = (byte)(0xff&(num>>24));

		return bytes;
	}

	/**
	 * 四个字节的字节数据转换成一个整形数据
	 * @param bytes 4个字节的字节数组
	 * @return 一个整型数据
	 */
	public static int ByteToInt(byte[] bytes) {
		int num = 0;
		int temp;
		temp = (0x000000ff & (bytes[0]))<<0;
		num = num | temp;
		temp = (0x000000ff & (bytes[1]))<<8;
		num = num | temp;
		temp = (0x000000ff & (bytes[2]))<<16;
		num = num | temp;
		temp = (0x000000ff & (bytes[3]))<<24;
		num = num | temp;

		return num;
	}

	public static byte[] LongToByte(long num) {
		byte[] bytes = new byte[8];

		for(int i=0; i<8; i++) {
			bytes[i] = (byte)(0xff&(num>>(i*8)));
		}

		return bytes;
	}
	public static byte[] asUnsigned32ByteArray(BigInteger n) {
		return asUnsignedNByteArray(n, 32);
	}
	public static byte[] asUnsignedNByteArray(BigInteger x, int length) {
		if(x == null) {
			return null;
		}

		byte[] tmp = new byte[length];
		int len = x.toByteArray().length;
		if(len > length+1) {
			return null;
		}

		if(len == length+1) {
			if(x.toByteArray()[0] != 0) {
				return null;
			} else {
				System.arraycopy(x.toByteArray(), 1, tmp, 0, length);
				return tmp;
			}
		} else {
			System.arraycopy(x.toByteArray(), 0, tmp, length-len, len);
			return tmp;
		}

	}
}
