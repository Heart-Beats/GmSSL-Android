package com.hl.gmssl.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hl.gmssl.android.ui.theme.GmSSLAndroidTheme
import com.hl.utils.copyAssets2Path
import org.gmssl.GmSSLJNI
import java.io.File
import java.util.Date

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContent {
			GmSSLAndroidTheme {
				// A surface container using the 'background' color from the theme
				Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

					val testString = getTestString()
					Greeting(testString)
				}
			}
		}
	}


	private fun getTestString(): String {
		return buildString {
			this.appendLine(GmSSLJNI.version_num())
			this.appendLine(GmSSLJNI.version_str())
			val key = ByteArray(GmSSLJNI.SM4_KEY_SIZE)
			GmSSLJNI.rand_bytes(key, 0, GmSSLJNI.SM4_KEY_SIZE.toLong())
			print_bytes(this, "rand_bytes(16)", key)
			val sm3_ctx: Long = GmSSLJNI.sm3_ctx_new()
			val dgst = ByteArray(GmSSLJNI.SM3_DIGEST_SIZE)
			GmSSLJNI.sm3_init(sm3_ctx)
			GmSSLJNI.sm3_update(sm3_ctx, "abc".toByteArray(), 0, 3)
			GmSSLJNI.sm3_finish(sm3_ctx, dgst)
			print_bytes(this, "sm3('abc')", dgst)
			val sm3_hmac_ctx: Long = GmSSLJNI.sm3_hmac_ctx_new()
			val hmac = ByteArray(GmSSLJNI.SM3_HMAC_SIZE)
			GmSSLJNI.sm3_hmac_init(sm3_hmac_ctx, key)
			GmSSLJNI.sm3_hmac_update(sm3_hmac_ctx, "abc".toByteArray(), 0, 3)
			GmSSLJNI.sm3_hmac_finish(sm3_hmac_ctx, hmac)
			print_bytes(this, "sm3_hmac('abc')", hmac)
			val password = "P@ssw0rd"
			val salt = ByteArray(GmSSLJNI.SM3_PBKDF2_MAX_SALT_SIZE)
			GmSSLJNI.rand_bytes(salt, 0, salt.size.toLong())
			val derived_key: ByteArray = GmSSLJNI.sm3_pbkdf2(password, salt, GmSSLJNI.SM3_PBKDF2_MIN_ITER, 16)
			print_bytes(this, "sm2_pbkdf2", derived_key)
			val sm4_key: Long = GmSSLJNI.sm4_key_new()
			GmSSLJNI.sm4_set_encrypt_key(sm4_key, key)
			val block = ByteArray(GmSSLJNI.SM4_BLOCK_SIZE)
			GmSSLJNI.rand_bytes(block, 0, block.size.toLong())
			print_bytes(this, "sm4 plain", block)
			val out_block = ByteArray(GmSSLJNI.SM4_BLOCK_SIZE)
			GmSSLJNI.sm4_encrypt(sm4_key, block, 0, out_block, 0)
			GmSSLJNI.sm4_set_decrypt_key(sm4_key, key)
			val plain_block = ByteArray(GmSSLJNI.SM4_BLOCK_SIZE)
			GmSSLJNI.sm4_encrypt(sm4_key, out_block, 0, plain_block, 0)
			print_bytes(this, "sm4 decrypt", plain_block)
			val iv = ByteArray(GmSSLJNI.SM4_BLOCK_SIZE)
			val buf = ByteArray(100)
			val plain = ByteArray(100)
			var outlen: Int
			var left: Int
			val sm4_cbc_ctx: Long = GmSSLJNI.sm4_cbc_ctx_new()
			GmSSLJNI.sm4_cbc_encrypt_init(sm4_cbc_ctx, key, iv)
			outlen = GmSSLJNI.sm4_cbc_encrypt_update(sm4_cbc_ctx, "abc".toByteArray(), 0, 3, buf, 0)
			left = GmSSLJNI.sm4_cbc_encrypt_finish(sm4_cbc_ctx, buf, outlen)
			var cipherlen: Int = outlen + left
			print_bytes_ex(this, "ciphertext", buf, 0, cipherlen)
			GmSSLJNI.sm4_cbc_decrypt_init(sm4_cbc_ctx, key, iv)
			outlen = GmSSLJNI.sm4_cbc_decrypt_update(sm4_cbc_ctx, buf, 0, cipherlen, plain, 0)
			left = GmSSLJNI.sm4_cbc_decrypt_finish(sm4_cbc_ctx, plain, outlen)
			var plainlen: Int = outlen + left
			print_bytes_ex(this, "plaintext", plain, 0, plainlen)
			val sm4_ctr_ctx: Long = GmSSLJNI.sm4_ctr_ctx_new()
			GmSSLJNI.sm4_ctr_encrypt_init(sm4_ctr_ctx, key, iv)
			outlen = GmSSLJNI.sm4_ctr_encrypt_update(sm4_ctr_ctx, "abc".toByteArray(), 0, 3, buf, 0)
			left = GmSSLJNI.sm4_ctr_encrypt_finish(sm4_ctr_ctx, buf, outlen)
			cipherlen = outlen + left
			print_bytes_ex(this, "ciphertext", buf, 0, cipherlen)
			GmSSLJNI.sm4_ctr_decrypt_init(sm4_ctr_ctx, key, iv)
			outlen = GmSSLJNI.sm4_ctr_decrypt_update(sm4_ctr_ctx, buf, 0, cipherlen, plain, 0)
			left = GmSSLJNI.sm4_ctr_decrypt_finish(sm4_ctr_ctx, plain, outlen)
			plainlen = outlen + left
			print_bytes_ex(this, "plaintext", plain, 0, plainlen)
			val sm4_gcm_ctx: Long = GmSSLJNI.sm4_gcm_ctx_new()
			val aad = "aad".toByteArray()
			GmSSLJNI.sm4_gcm_encrypt_init(sm4_gcm_ctx, key, iv, aad, GmSSLJNI.SM4_GCM_MAX_TAG_SIZE)
			outlen = GmSSLJNI.sm4_gcm_encrypt_update(sm4_gcm_ctx, "abc".toByteArray(), 0, 3, buf, 0)
			left = GmSSLJNI.sm4_gcm_encrypt_finish(sm4_gcm_ctx, buf, outlen)
			cipherlen = outlen + left
			print_bytes_ex(this, "gcm ciphertext", buf, 0, cipherlen)
			GmSSLJNI.sm4_gcm_decrypt_init(sm4_gcm_ctx, key, iv, aad, GmSSLJNI.SM4_GCM_MAX_TAG_SIZE)
			outlen = GmSSLJNI.sm4_gcm_decrypt_update(sm4_gcm_ctx, buf, 0, cipherlen, plain, 0)
			left = GmSSLJNI.sm4_gcm_decrypt_finish(sm4_gcm_ctx, plain, outlen)
			plainlen = outlen + left
			print_bytes_ex(this, "gcm plaintext", plain, 0, plainlen)
			val pass = "123456"
			val z = ByteArray(32)
			var verify_ret: Int
			var sm2_key: Long = GmSSLJNI.sm2_key_generate()

			val externalFilesDir = getExternalFilesDir(null)

			val sm2pem = copyAssets2Path("sm2.pem", File(externalFilesDir, "sm2.pem").absolutePath)
			val sm2pubPem = copyAssets2Path("sm2pub.pem", File(externalFilesDir, "sm2pub.pem").absolutePath)

			GmSSLJNI.sm2_private_key_info_encrypt_to_pem(sm2_key, pass, sm2pem)
			sm2_key = GmSSLJNI.sm2_private_key_info_decrypt_from_pem(pass, sm2pem)
			GmSSLJNI.sm2_public_key_info_to_pem(sm2_key, sm2pubPem)
			var sm2_pub: Long = GmSSLJNI.sm2_public_key_info_from_pem(sm2pubPem)
			GmSSLJNI.sm2_compute_z(sm2_pub, GmSSLJNI.SM2_DEFAULT_ID, z)
			print_bytes(this, "z", z)
			var sig: ByteArray = GmSSLJNI.sm2_sign(sm2_key, dgst)
			verify_ret = GmSSLJNI.sm2_verify(sm2_pub, dgst, sig)
			this.appendLine(verify_ret)
			val sm2_sign_ctx: Long = GmSSLJNI.sm2_sign_ctx_new()
			GmSSLJNI.sm2_sign_init(sm2_sign_ctx, sm2_key, GmSSLJNI.SM2_DEFAULT_ID)
			GmSSLJNI.sm2_sign_update(sm2_sign_ctx, "abc".toByteArray(), 0, 3)
			sig = GmSSLJNI.sm2_sign_finish(sm2_sign_ctx)
			GmSSLJNI.sm2_verify_init(sm2_sign_ctx, sm2_pub, GmSSLJNI.SM2_DEFAULT_ID)
			GmSSLJNI.sm2_verify_update(sm2_sign_ctx, "abc".toByteArray(), 0, 3)
			verify_ret = GmSSLJNI.sm2_verify_finish(sm2_sign_ctx, sig)
			this.appendLine(verify_ret)
			val sm2_cipher: ByteArray = GmSSLJNI.sm2_encrypt(sm2_pub, "abc".toByteArray())
			val sm2_plain: ByteArray = GmSSLJNI.sm2_decrypt(sm2_key, sm2_cipher)
			print_bytes(this, "sm2_plain", sm2_plain)
			val sm9_sig: ByteArray
			var sm9_master: Long = GmSSLJNI.sm9_sign_master_key_generate()

			val sm9pem = sm2pem
			val sm9pubPem = sm2pubPem
			val sm9keyPem = sm9pem

			GmSSLJNI.sm9_sign_master_key_info_encrypt_to_pem(sm9_master, "1234", sm9pem)
			sm9_master = GmSSLJNI.sm9_sign_master_key_info_decrypt_from_pem("1234", sm9pem)
			GmSSLJNI.sm9_sign_master_public_key_to_pem(sm9_master, sm9pubPem)
			var sm9_master_pub: Long = GmSSLJNI.sm9_sign_master_public_key_from_pem(sm9pubPem)
			var sm9_key: Long = GmSSLJNI.sm9_sign_master_key_extract_key(sm9_master, "Alice")
			GmSSLJNI.sm9_sign_key_info_encrypt_to_pem(sm9_key, "1234", sm9keyPem)
			sm9_key = GmSSLJNI.sm9_sign_key_info_decrypt_from_pem("1234", sm9keyPem)
			val sm9_ctx: Long = GmSSLJNI.sm9_sign_ctx_new()
			GmSSLJNI.sm9_sign_init(sm9_ctx)
			GmSSLJNI.sm9_sign_update(sm9_ctx, "abc".toByteArray(), 0, 3)
			sm9_sig = GmSSLJNI.sm9_sign_finish(sm9_ctx, sm9_key)
			GmSSLJNI.sm9_verify_init(sm9_ctx)
			GmSSLJNI.sm9_verify_update(sm9_ctx, "abc".toByteArray(), 0, 3)
			verify_ret = GmSSLJNI.sm9_verify_finish(sm9_ctx, sm9_sig, sm9_master_pub, "Alice")
			this.appendLine(verify_ret)
			sm9_master = GmSSLJNI.sm9_enc_master_key_generate()
			GmSSLJNI.sm9_enc_master_key_info_encrypt_to_pem(sm9_master, "1234", sm9pem)
			sm9_master = GmSSLJNI.sm9_enc_master_key_info_decrypt_from_pem("1234", sm9pem)
			GmSSLJNI.sm9_enc_master_public_key_to_pem(sm9_master, sm9pubPem)
			sm9_master_pub = GmSSLJNI.sm9_enc_master_public_key_from_pem(sm9pubPem)
			sm9_key = GmSSLJNI.sm9_enc_master_key_extract_key(sm9_master, "Alice")
			GmSSLJNI.sm9_enc_key_info_encrypt_to_pem(sm9_key, "1234", sm9keyPem)
			sm9_key = GmSSLJNI.sm9_enc_key_info_decrypt_from_pem("1234", sm9keyPem)
			val sm9_cipher: ByteArray = GmSSLJNI.sm9_encrypt(sm9_master_pub, "Alice", "abc".toByteArray())
			val sm9_plain: ByteArray = GmSSLJNI.sm9_decrypt(sm9_key, "Alice", sm9_cipher)
			print_bytes(this, "sm9_plain", sm9_plain)

			val rootCAPem = copyAssets2Path("ROOTCA.pem", File(externalFilesDir, "ROOTCA.pem").absolutePath)
			val cert: ByteArray = GmSSLJNI.cert_from_pem(rootCAPem)
			GmSSLJNI.cert_to_pem(cert, "cert.pem")
			val serial: ByteArray = GmSSLJNI.cert_get_serial_number(cert)
			print_bytes(this, "serialNumber", serial)
			val subject: Array<String> = GmSSLJNI.cert_get_subject(cert)
			var i: Int = 0
			while (i < subject.size) {
				this.appendLine("  " + subject[i])
				i++
			}
			val issuer: Array<String> = GmSSLJNI.cert_get_subject(cert)
			i = 0
			while (i < issuer.size) {
				this.appendLine("  " + issuer[i])
				i++
			}
			val not_before: Long = GmSSLJNI.cert_get_not_before(cert)
			val not_after: Long = GmSSLJNI.cert_get_not_after(cert)
			this.appendLine(not_before)
			this.appendLine("not_before " + Date(not_before * 1000))
			this.appendLine("not_after " + Date(not_after * 1000))
			sm2_pub = GmSSLJNI.cert_get_subject_public_key(cert)
			val cert_verify: Int = GmSSLJNI.cert_verify_by_ca_cert(cert, cert, GmSSLJNI.SM2_DEFAULT_ID)
			this.appendLine("verify result $cert_verify")
		}
	}

	private fun print_bytes(stringBuilder: StringBuilder, label: String?, data: ByteArray) {
		stringBuilder.append(String.format("%s: ", label))
		var i = 0
		while (i < data.size) {
			stringBuilder.append(String.format("%02x", data[i]))
			i++
		}
		stringBuilder.appendLine()
	}

	private fun print_bytes_ex(stringBuilder: StringBuilder, label: String?, data: ByteArray, offset: Int, len: Int) {
		stringBuilder.append(String.format("%s: ", label))
		var i = 0
		while (i < len) {
			stringBuilder.append(String.format("%02x", data[offset + i]))
			i++
		}
		stringBuilder.appendLine()
	}
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
	val rememberScrollState = rememberScrollState()

	Text(
		text = "$name",
		modifier = modifier
			.padding(10.dp)
			.verticalScroll(rememberScrollState)
	)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
	GmSSLAndroidTheme {
		Greeting("Android")
	}
}