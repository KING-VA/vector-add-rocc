package vector_add_rocc

import chisel3._
import chisel3.util._
import org.chipsalliance.cde.config._
import freechips.rocketchip.tile._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.rocket._
import freechips.rocketchip.tilelink._

object Consts {
  val len = 64
}

//Create custom bundle for ChiselTest purposes
class Command extends Bundle {
  val inst = new RoCCInstruction
  val rs1 = Bits(Consts.len.W)
  val rs2 = Bits(Consts.len.W)
  val status = new MStatus //unused, for convenient LazyRoCC connection
}

class Response extends Bundle {
  val rd = Bits(5.W)
  val data = Bits(Consts.len.W)
}

class VectorAdd()(implicit p: Parameters) extends Module {

  val io = IO(new Bundle {
	  val cmd = Flipped(Decoupled(new Command))
	  val resp = Decoupled(new Response)
	  val busy = Output(Bool())
  })
 
  // The parts of the command are as follows
  // inst - the parts of the instruction itself
  //   opcode
  //   rd - destination register number
  //   rs1 - first source register number
  //   rs2 - second source register number
  //   funct
  //   xd - is the destination register being used?
  //   xs1 - is the first source register being used?
  //   xs2 - is the second source register being used?
  // rs1 - the value of source register 1
  // rs2 - the value of source register 2

  /* Instantiating Wires and Regs */
  val in1_vec_wire = WireInit(VecInit(Seq.fill(8) {0.U(8.W)}))
  val in2_vec_wire = WireInit(VecInit(Seq.fill(8) {0.U(8.W)}))
  val sum_vec_wire = WireInit(VecInit(Seq.fill(8) {0.U(8.W)}))
 
  val cmd_bits_reg = RegInit(0.U.asTypeOf(new Command))
  val state_reg = RegInit(0.U(1.W))

  /* in ready */
  io.cmd.ready := ~state_reg
  /* busy */
  io.busy := state_reg
  /* out valid */  
  io.resp.valid := state_reg

  when (state_reg === 0.U) {
    when (io.cmd.fire) {
      state_reg := ~state_reg
      cmd_bits_reg := io.cmd.bits
    }    
  }.otherwise {
    when (io.resp.fire) {
      state_reg := ~state_reg
    }    
  }
    
  io.resp.bits.rd := cmd_bits_reg.inst.rd
  io.resp.bits.data := sum_vec_wire.asTypeOf(UInt(Consts.len.W))

  /* Set up inputs */
  in1_vec_wire := cmd_bits_reg.rs1.asTypeOf(in1_vec_wire)
  in2_vec_wire := cmd_bits_reg.rs2.asTypeOf(in2_vec_wire)
 
  /* Performing sum */
  for (i <- 0 until 8) {
    sum_vec_wire(i) := in1_vec_wire(i) + in2_vec_wire(i)
  }

  /* printf for debugging in sim */
  // printf("sum_vec_wire = %x \n", sum_vec_wire.asUInt) // example C-style printf
}